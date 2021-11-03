package com.polosochka;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public class PWindow {
    private final Bitmap screen;

    private final JFrame frame;
    private final MainCanvas canvas;
    private final BufferedImage screenImage;

    private int width;
    private int height;

    private int contentWidth;
    private int contentHeight;
    private int contentX;
    private int contentY;

    private boolean running, fullscreen;

    static {
        try {
            @SuppressWarnings("unused")
            Toolkit toolkit = Toolkit.getDefaultToolkit();
        } catch (Exception e) {
            Logger.reportError("WindowImpl", "Problems with cursor creating.", e);
        }
    }

    public PWindow(String name, int windowWidth, int windowHeight, DisplayMode dm, Consumer<KeyEvent> eventHandler, Bitmap bckg) {
        screenImage = new BufferedImage(dm.getContentWidth(), dm.getContentHeight(), BufferedImage.TYPE_INT_ARGB);

        screen = Utils.getLinkedBitmap(screenImage);

        if (bckg != null) {
            screen.draw_SC(bckg, 0, 0, 1, 1);
        }

        this.width = windowWidth;
        this.height = windowHeight;

        frame = new JFrame(name);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);

        PWindow w = this;

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (!w.fullscreen) {
                    width = frame.getWidth();
                    height = frame.getHeight();
                }
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                stopMainLoop();
            }
        });

        canvas = new MainCanvas();
        frame.add(canvas);

        canvas.setFocusTraversalKeysEnabled(false);

        canvas.setFocusable(true);

        canvas.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                reshape(e.getComponent().getWidth(), e.getComponent().getHeight());
            }
        });

        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (eventHandler != null) {
                    eventHandler.accept(keyEvent);
                }
            }
        });

        if (fullscreen) {
            setFullscreen(fullscreen, false);
        } else {
            frame.setVisible(true);
        }
    }

    public void startMainLoop() throws Exception {
        try {
            canvas.draw(screenImage);
            running = true;

            while (running) {
                double lastTime = System.nanoTime() / 1000_000_000.0;

                double toSleep = StripeController.action(screen);
                canvas.draw(screenImage);

                double delta = (System.nanoTime() / 1000_000_000.0) - lastTime;

                Thread.sleep((long) (Math.max(toSleep - delta, 0) * 1000.0));
            }
        } catch (Exception e) {
            Logger.warn("WindowImpl", "Exception in window main loop." + e.getClass().getSimpleName());

            throw e;
        }
    }

    private void setFullscreen(boolean fullscreen) {
        setFullscreen(fullscreen, true);
    }

    private void setFullscreen(boolean fullscreen, boolean initialized) {
        if (this.fullscreen == fullscreen) {
            return;
        } else {
            this.fullscreen = fullscreen;

            if (fullscreen) {
                if (initialized) {
                    frame.dispose();
                }

                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                frame.setUndecorated(true);

                frame.setVisible(true);
            } else {
                if (initialized) {
                    frame.dispose();
                }

                frame.setExtendedState(0);
                frame.setSize(width, height);
                frame.setLocationRelativeTo(null);
                frame.setUndecorated(false);

                frame.setVisible(true);
            }
        }
    }

    private void reshape(int newWidth, int newHeight) {

        final double aspect = (double) newWidth / (double) newHeight;
        final double targetAspect = (double) screen.width / (double) screen.height;

        double scale = (double) newHeight / (double) screen.height;
        double wrongScale = (double) newWidth / (double) screen.width;

        if (aspect > targetAspect) {
            //If window is too wide

            contentWidth = (int) ((double) screen.width * scale);
            contentHeight = (int) ((double) screen.height * scale);

            contentX = ((int) ((double) screen.width * wrongScale) - contentWidth) / 2;
            contentY = 0;
        } else {
            //if window is too high

            /*Swap value*/
            {
                final double tmp = scale;
                scale = wrongScale;
                wrongScale = tmp;
            }

            contentWidth = (int) ((double) screen.width * scale);
            contentHeight = (int) ((double) screen.height * scale);

            contentX = 0;
            contentY = ((int) ((double) screen.height * wrongScale) - contentHeight) / 2;
        }
    }


    public int getWidth() {
        return frame.getWidth();
    }

    public int getHeight() {
        return frame.getHeight();
    }

    public void stopMainLoop() {
        running = false;
        frame.dispose();
    }

    private class MainCanvas extends Canvas {
        private static final long serialVersionUID = 1L;

        private void draw(BufferedImage data) {
            BufferStrategy bs = this.getBufferStrategy();

            if (bs == null) {
                //Double buffering
                this.createBufferStrategy(2);
                return;
            }

            try {
                Graphics g = bs.getDrawGraphics();

                Graphics2D gg = (Graphics2D) g;

                gg.setColor(Color.BLACK);

                gg.fillRect(0, 0, getWidth(), getHeight());

                //Hearth of the game graphics
                gg.drawImage(data, contentX, contentY, contentWidth, contentHeight, null);

                gg.dispose();
                bs.show();
            } catch (IllegalStateException ignored) {
            }
        }
    }
}

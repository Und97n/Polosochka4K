import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class PWindow {
    public static final boolean SHOW_FPS_OPTION = true;

    private static Cursor transparentCursor;
    private static Robot robot;

    private final Bitmap screen;

    private final JFrame frame;
    private final MainCanvas canvas;
    private final BufferedImage screenImage;

    // Text in upper right corner of window.
    private final String upperRightText;

    private int width;
    private int height;

    private DisplayMode dm;

    private int contentWidth;
    private int contentHeight;
    private int contentX;
    private int contentY;

    private boolean running, fullscreen;

    private boolean contentUpdate;

    private double mouseScreenX;
    private double mouseScreenY;

    private double mouseX;
    private double mouseY;

    private double mouseDx;
    private double mouseDy;

    private double realFPS;

    static {
        try {
            @SuppressWarnings("unused")
            Toolkit toolkit = Toolkit.getDefaultToolkit();

            // Transparent 16 x 16 pixel cursor image.
            BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

            // Create a new blank cursor.
            transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                    cursorImg, new Point(0, 0), "blank cursor");

            //Default game cursor
            //Image image = Utils.loadBuf("data/textures/pointer1.png");
            //cursor1 = toolkit.createCustomCursor(image , new Point(image.getWidth(null) / 2, image.getHeight(null) / 2), "Cursor 1");

        } catch (Exception e) {
            Logger.reportError("WindowImpl", "Problems with cursor creating.", e);
        }

        try {
            robot = new Robot();
        } catch (Exception e) {
            Logger.reportError("WindowImpl", "Problems with java.awt.Robot.", e);
        }
    }

    public PWindow(String name, String upperRightText, int windowWidth, int windowHeight, DisplayMode dm) {
        this.upperRightText = upperRightText;

        this.dm = dm;

        screenImage = new BufferedImage(dm.getContentWidth(), dm.getContentHeight(), BufferedImage.TYPE_INT_ARGB);

        screen = Utils.getLinkedBitmap(screenImage);

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

            @Override
            public void windowActivated(WindowEvent we) {
                contentUpdate = true;
            }

            @Override
            public void windowDeactivated(WindowEvent we) {
//				 contentUpdate = false;
            }
        });

        canvas = new MainCanvas();
        frame.add(canvas);

        canvas.setFocusTraversalKeysEnabled(false);
//
//        JDInputHandler ih = new JDInputHandler();
//
//        inputSystem = new InputSystem(ih);
//        ih.link(canvas);

        canvas.setFocusable(true);

        canvas.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                reshape(e.getComponent().getWidth(), e.getComponent().getHeight());
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
            running = true;

            //Time in seconds
            double lastTime = System.nanoTime() / 1000_000_000.0;
            double deltaForTick = 0, deltaForDraw = 0;

            //For fps measuring
            double lastFPSCheck = 0;
            long framesPerSecond = 0;

            while (running) {
                //Time between cycles
                double delta = (System.nanoTime() / 1000_000_000.0) - lastTime;

                lastTime = System.nanoTime() / 1000_000_000.0;

                StripeController.action(delta, screen);

                double currentTime = (System.nanoTime() / 1000_000_000.0);

                //Math real fps
                if ((currentTime - lastFPSCheck) >= 1) {
                    realFPS = (double) (framesPerSecond) / (double) (currentTime - lastFPSCheck);

                    framesPerSecond = 0;
                    lastFPSCheck = currentTime;
                }
                canvas.draw(screenImage);
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
    public Bitmap copyScreen() {
        return screen.copy();
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
            ;

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
                //Double fuffering
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

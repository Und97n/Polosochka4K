package com.polosochka;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class StripeController {
    private static Configuration conf;

    private static int stripeOrderCounter = 0;

    public static void action(double timeDelta, Bitmap screen) throws InterruptedException {
        if (conf != null) {
            synchronized (StripeController.class) {
                if (stripeOrderCounter <= 0 && conf.background != null) {
                    screen.draw_SC(conf.background, 0, 0, 1 ,1);
                }

                if (stripeOrderCounter >= 0 && stripeOrderCounter < conf.lines.length) {
                    assert (screen.width == conf.image.width) && (screen.height == conf.image.height);

                    Stripe s = conf.lines[stripeOrderCounter++];

                    s.draw(conf.image, screen);

                    double currentStripeTime = s.getDelay();
//            double sleepTime = Utils.border(currentStripeTime - timeDelta, 0, 1);
                    double sleepTime = Math.max(currentStripeTime - timeDelta, 0);

                    Thread.sleep((long) (sleepTime * 1000));
                } else {
                    Thread.sleep(17);
                }
            }
        }
    }

    private static void eventHandler(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_C) {
            stripeOrderCounter = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            stripeOrderCounter = 0;
        }
//        else if (e.getKeyChar() == 'r') {
//            try {
//                synchronized (StripeController.class) {
//                    conf = new Configuration();
//                    conf.readConfigFile("config.txt");
//                }
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            } catch (Utils.FileLoadingException ex) {
//                JOptionPane.showMessageDialog(null, ex.toString());
//                System.exit(1);
//            }
//        }
    }

    public static void main(String[] args) throws Exception {
        conf = new Configuration();
        conf.readConfigFile("config.txt");
        Stripe.prepareStripes(conf);

        new PWindow("StripeTester", "", 1000, 720, new DisplayMode(conf.image.width, conf.image.height), StripeController::eventHandler).startMainLoop();
    }
}

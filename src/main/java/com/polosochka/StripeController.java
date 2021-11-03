package com.polosochka;

import java.awt.event.KeyEvent;

public class StripeController {
    private static Configuration conf;

    private static int stripeOrderCounter = -1;

    public static double action(Bitmap screen) {
        if (conf != null) {
            if (stripeOrderCounter <= 0 && conf.background != null) {
                screen.draw_SC(conf.background, 0, 0, 1, 1);
            }

            if (stripeOrderCounter >= 0 && stripeOrderCounter < conf.lines.length) {
                assert (screen.width == conf.image.width) && (screen.height == conf.image.height);

                Stripe s = conf.lines[stripeOrderCounter++];

                s.draw(conf.image, screen);

                return s.getDelay();
            }
        }

        return 0.02;
    }

    private static void eventHandler(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_C) {
            stripeOrderCounter = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            stripeOrderCounter = 0;
        }
    }

    public static void main(String[] args) throws Exception {
        conf = new Configuration();
        conf.readConfigFile("config.txt");
        Stripe.prepareStripes(conf);

        new PWindow("StripeTester", 1000, 720, new DisplayMode(conf.image.width, conf.image.height), StripeController::eventHandler, conf.background).startMainLoop();
    }
}

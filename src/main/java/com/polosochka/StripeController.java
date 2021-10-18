package com.polosochka;

public class StripeController {
    private static Configuration conf;

    private static int stripeOrderCounter = 0;

    public static void action(double timeDelta, Bitmap screen) throws InterruptedException {
        if (stripeOrderCounter < conf.lines.length - 1) {
            ++stripeOrderCounter;
            assert (screen.width == conf.image.width) && (screen.height == conf.image.height);

            Stripe s = conf.lines[stripeOrderCounter];

            s.draw(conf.image, screen);

            double currentStripeTime = s.getDelay();
//            double sleepTime = Utils.border(currentStripeTime - timeDelta, 0, 1);
            double sleepTime = Math.max(currentStripeTime - timeDelta, 0);

            Thread.sleep((long) (sleepTime * 1000));
        } else {
            Thread.sleep(17);
        }
    }

    public static void main(String[] args) throws Exception {
        conf = new Configuration();
        conf.readConfigFile("config.txt");
        Stripe.prepareStripes(conf);

        new PWindow("StripeTester", "", 1000, 720, new DisplayMode(conf.image.width, conf.image.height)).startMainLoop();
    }
}

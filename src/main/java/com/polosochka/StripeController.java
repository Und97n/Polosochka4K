package com.polosochka;

import static com.polosochka.Configuration.Line;

public class StripeController {
    private static Bitmap ourImage;
    private static int actualStripeIndex = 0;
    private static int stripeOrderCounter = -1;

    private static double stripeHeightCounter = 0;

    //    private static final double stripeHeight = 2.52;
    private static double stripeHeight;

    // We have ARGB color, each byte of int is a color component
    public static int mixColor(int orig, double transparency) {
        assert transparency >= 0 && transparency <= 1;
        return (orig & 0xffffff) | ((int) (transparency * 255) << 24);
    }

    public static void action(double timeDelta, Bitmap screen) throws InterruptedException {
        if (stripeOrderCounter < Configuration.lines.size() - 1) {
            ++stripeOrderCounter;
            assert (screen.width == ourImage.width) && (screen.height == ourImage.height);
            final int w = ourImage.width, h = ourImage.height;

            Line line = Configuration.lines.get(stripeOrderCounter);
            double stripeTransparency = Math.abs(Math.sin(4.0 * stripeOrderCounter / (double) screen.height));

            stripeHeightCounter += stripeHeight;
//            actualStripeIndex = (int) stripeHeight * stripeOrderCounter;

            // For the stripe height floating precision we should use two vars - stripeHeightCounter and stripeCounter
            while (actualStripeIndex < h && stripeHeightCounter >= 1) {
                // Draw stripe
                for (int x = 0; x < w; ++x) {
                    screen.pixels[w * actualStripeIndex + x] = mixColor(ourImage.pixels[w * actualStripeIndex + x], stripeTransparency);
                }

                ++actualStripeIndex;
                stripeHeightCounter -= 1.0;
            }
            double currentStripeTime = line.delay;
//            double sleepTime = Utils.border(currentStripeTime - timeDelta, 0, 1);
            double sleepTime = Math.max(currentStripeTime - timeDelta, 0);

            Thread.sleep((long) (sleepTime * 1000));
        } else {
            Thread.sleep(17);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration.readConfigFile("config.txt");
        ourImage = Utils.loadBitmap("example.png");
        stripeHeight = ourImage.height / (double) Configuration.lines.size();

        new PWindow("StripeTester", "", 1000, 720, new DisplayMode(ourImage.width, ourImage.height)).startMainLoop();
    }
}

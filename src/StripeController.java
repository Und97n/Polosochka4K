public class StripeController {
    private static Bitmap ourImage;
    private static int stripeCounter = 0;

    private static double stripeHeightCounter = 0;

    private static final double stripeHeight = 2.52;

    // We have ARGB color, each byte of int is a color component
    public static int mixColor(int orig, double transparency) {
        assert transparency >= 0 && transparency <= 1;
        return (orig & 0xffffff) | ((int)(transparency * 255) << 24);
    }

    public static void action(double timeDelta, Bitmap screen) throws InterruptedException {
        assert (screen.width == ourImage.width) && (screen.height == ourImage.height);
        final int w = ourImage.width, h = ourImage.height;

        double stripeTransparency = Math.abs(Math.sin(4.0 * stripeCounter / (double) screen.height));

        if (stripeCounter < h) {
            stripeHeightCounter += stripeHeight;

            // For the stripe height floating precision we should use two vars - stripeHeightCounter and stripeCounter
            while (stripeCounter < h && stripeHeightCounter > 1) {
                // Draw stripe
                for (int x = 0; x < w; ++x) {
                    screen.pixels[w*stripeCounter + x] = mixColor(ourImage.pixels[w*stripeCounter + x], stripeTransparency);
                }

                ++stripeCounter;
                stripeHeightCounter -= 1.0;
            }
        }

        double sleepTime = Utils.border(0.016 - timeDelta, 0, 1);

        Thread.sleep((long) (sleepTime * 1000));
    }

    public static void main(String[] args) throws Exception {
        ourImage = Utils.loadBitmap("example.png");

        new PWindow("StripeTester", "", 1280, 720, new DisplayMode(ourImage.width, ourImage.height)).startMainLoop();
    }
}

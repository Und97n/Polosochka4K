package com.polosochka;

import java.util.Collection;

public class Stripe {
    private final int index;
    private final double delay, transparency;

    private int drawCacheSY = -1, drawCacheEY = -1;

    public Stripe(int index, double delay, double transparency) {
        this.index = index;
        this.delay = delay;
        this.transparency = transparency;
    }

    // We have ARGB color, each byte of int is a color component
    public static int mixColor(int bckg, int color, double alpha) {
        assert alpha >= 0 && alpha <= 1;
        int r1 = 0xFF & (color >> 16);
        int g1 = 0xFF & (color >> 8);
        int b1 = 0xFF & (color);

        int r2 = 0xFF & (bckg >> 16);
        int g2 = 0xFF & (bckg >> 8);
        int b2 = 0xFF & (bckg);

        int r = (int) Utils.border(r1 * alpha + r2 * (1.0 - alpha), 0, 255);
        int g = (int) Utils.border(g1 * alpha + g2 * (1.0 - alpha), 0, 255);
        int b = (int) Utils.border(b1 * alpha + b2 * (1.0 - alpha), 0, 255);

        int resultColor = 0xff000000 | (r << 16) | (g << 8) | b;

        return resultColor;
    }

    public void draw(Bitmap sourceImage, Bitmap screen) {
        final int w = screen.width;
        for (int y = drawCacheSY; y < drawCacheEY; ++y) {
            for (int x = 0; x < w; ++x) {
                screen.pixels[w * y + x] = mixColor(screen.pixels[w * y + x], sourceImage.pixels[w * y + x], transparency);
            }
        }
    }

    // Put draw information into stripes objects
    public static void prepareStripes(Configuration conf) {
        double stripeHeightCounter = 0;
        int actualStripeIndex = 0;
        final int w = conf.image.width, h = conf.image.height;

        for (Stripe s: conf.linesSorted) {
            stripeHeightCounter += conf.stripeHeight;

            s.drawCacheSY = actualStripeIndex;

            final int delta = (int) stripeHeightCounter;
            stripeHeightCounter -= delta;
            actualStripeIndex += delta;

            s.drawCacheEY = actualStripeIndex;
        }
    }

    public int getIndex() {
        return index;
    }

    public double getDelay() {
        return delay;
    }
}

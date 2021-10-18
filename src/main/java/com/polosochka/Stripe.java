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
    public static int mixColor(int orig, double transparency) {
        assert transparency >= 0 && transparency <= 1;
        return (orig & 0xffffff) | ((int) (transparency * 255) << 24);
    }

    public void draw(Bitmap sourceImage, Bitmap screen) {
        final int w = screen.width;
        for (int y = drawCacheSY; y < drawCacheEY; ++y) {
            for (int x = 0; x < w; ++x) {
                screen.pixels[w * y + x] = mixColor(sourceImage.pixels[w * y + x], transparency);
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

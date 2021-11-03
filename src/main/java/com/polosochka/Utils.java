package com.polosochka;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Utils {
    public static class FileException extends Exception {
        private static final long serialVersionUID = 1L;

        public FileException(String message, Exception cause, String filePath) {
            super(message + " Path: \"" + filePath + '"', cause);
        }
    }

    public static class FileLoadingException extends FileException {
        private static final long serialVersionUID = 1L;

        public FileLoadingException(String message, Exception cause, String filePath) {
            super(message, cause, filePath);
        }
    }

    public static BufferedImage loadImage(String s) throws FileLoadingException {
        if (s == null) {
            throw new NullPointerException("File path is null.");
        }

        Image imageX;

        try {
            imageX = new ImageIcon(s).getImage();
        } catch (Exception e) {
            throw new FileLoadingException("Problems with image loading.", e, s);
        }

        if (imageX.getWidth(null) == -1 || imageX.getHeight(null) == -1) {
            throw new FileLoadingException("Problems with image loading.", null, s);
        }

        BufferedImage bl = new BufferedImage(imageX.getWidth(null),
                imageX.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        //Strange tools
        Graphics2D ig = bl.createGraphics();
        ig.drawImage(imageX, 0, 0, null);
        ig.dispose();

        return bl;
    }

    public static Bitmap loadBitmap(String filePath) throws FileLoadingException {
        BufferedImage source = loadImage(filePath);

        // BufferedImage source is always in ARGB integer format
        int[] imagePixels = ((DataBufferInt) source.getRaster().getDataBuffer()).getData();

        int[] pixels = new int[imagePixels.length];

        System.arraycopy(imagePixels, 0, pixels, 0, imagePixels.length);

        Bitmap ret = new Bitmap(pixels, source.getWidth(), source.getHeight());

        return ret;
    }

    public static Bitmap getLinkedBitmap(BufferedImage image) {
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        return new Bitmap(pixels, image.getWidth(), image.getHeight());
    }

    public static int border(int value, int minBorder, int maxBorder) {
        assert minBorder >= maxBorder;

        if (value < minBorder) {
            return minBorder;
        } else if (value > maxBorder) {
            return maxBorder;
        } else {
            return value;
        }
    }
}

package com.polosochka;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

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

    public static class FileSavingException extends FileException {
        private static final long serialVersionUID = 1L;

        public FileSavingException(String message, Exception cause, String path) {
            super(message, cause, path);
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

        //Don't worry: BufferedImage source is always in ARGB integer format
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

    public static boolean saveImage(BufferedImage i, String directory, String fileName, String format) throws FileSavingException {
        if (directory == null) {
            throw new NullPointerException("Directory path is null.");
        }

        if (fileName == null) {
            throw new NullPointerException("File name is null or empty.");
        }

        if (format == null) {
            throw new NullPointerException("Format is null or empty.");
        }

        File f = new File(directory + fileName + "." + format);

        if (f.isDirectory()) {
            throw new FileSavingException("File path is incorrect!", null, f.getPath());
        }

        try {
            File dir = new File(directory);

            if (!dir.exists()) {
                if (!dir.mkdir()) {
                    throw new FileSavingException("Cannot create directory!", null, f.getPath());
                }
            }

            return ImageIO.write(i, format, f);
        } catch (IOException e) {
            throw new FileSavingException("Problems with file saving!", e, f.getPath());
        }
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

    public static long border(long value, long minBorder, long maxBorder) {
        assert minBorder >= maxBorder;

        if (value < minBorder) {
            return minBorder;
        } else if (value > maxBorder) {
            return maxBorder;
        } else {
            return value;
        }
    }

    public static float border(float value, float minBorder, float maxBorder) {
        assert minBorder >= maxBorder;

        if (value < minBorder) {
            return minBorder;
        } else if (value > maxBorder) {
            return maxBorder;
        } else {
            return value;
        }
    }

    public static double border(double value, double minBorder, double maxBorder) {
        assert minBorder >= maxBorder;

        if (value < minBorder) {
            return minBorder;
        } else if (value > maxBorder) {
            return maxBorder;
        } else {
            return value;
        }
    }

    public static int intHash(int val) {
        int a = val;

        a -= (a << 6);
        a ^= (a >> 17);
        a -= (a << 9);
        a ^= (a << 4);
        a -= (a << 3);
        a ^= (a << 10);
        a ^= (a >> 15);

        return a;
    }
}

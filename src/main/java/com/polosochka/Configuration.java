package com.polosochka;

import javax.sound.sampled.Line;
import java.io.*;
import java.util.ArrayList;

public class Configuration {
    public Bitmap image;
    public Stripe[] lines;
    public Stripe[] linesSorted;
    public double stripeHeight;

    private int counter = 0;

    public void readConfigFile(String path) throws IOException, Utils.FileLoadingException {
        InputStream is = Configuration.class.getClassLoader().getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException();
        }

        readFromInputStream(is);
    }

    private void readFromInputStream(InputStream inputStream) throws IOException, Utils.FileLoadingException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String imagePath = br.readLine();

            if (imagePath == null) {
                throw new IOException("No path to the image!");
            }

            image = Utils.loadBitmap(imagePath);

            String strLine;
            strLine = br.readLine();
            if (strLine == null) {
                throw new IOException("Config file is empty!");
            }
            try {
                int n = Integer.parseInt(strLine);
                lines = new Stripe[n];
                linesSorted = new Stripe[n];
            } catch (NumberFormatException e) {
                throw new IOException("First config line is formatted incorrectly.");
            }

            while ((strLine = br.readLine()) != null) {
                strLine = strLine.trim();
                if (!strLine.isEmpty()) {
                    loadLine(strLine);
                }
            }

            stripeHeight = image.height / (double) lines.length;
        }

        // Fill missing stripes
        for (int index = 0; index < linesSorted.length; ++index) {
            if (linesSorted[index] == null) {
                Stripe s = new Stripe(index, 6d/linesSorted.length, 1);
                linesSorted[index] = s;
                lines[counter++] = s;
            }
        }
    }

    private void loadLine(String line) throws IOException {
        try {
            String[] args = line.split(":");
            if (args.length != 2 && args.length != 3) throw new NumberFormatException();

            Stripe s = new Stripe(Integer.parseInt(args[0]), Double.parseDouble(args[1]) / 1000d, args.length == 3 ? Double.parseDouble(args[2]) : 1);

            if (linesSorted[s.getIndex()] != null) {
                throw new IOException("Config line '" + line + "' is formatted incorrectly: stripe duplication (index '" + s.getIndex() + ").");
            }

            linesSorted[s.getIndex()] = s;
            lines[counter++] = s;
        } catch (NumberFormatException e) {
            throw new IOException("Config line '" + line + "' is formatted incorrectly.");
        }
    }
}

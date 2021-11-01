package com.polosochka;

import java.io.*;

public class Configuration {
    public Bitmap image, background;
    public Stripe[] lines;
    public Stripe[] linesSorted;
    public double stripeHeight;

    public double minDelta = 1.0 / 60.0;

    private int counter = 0;

    public void readConfigFile(String path) throws IOException, Utils.FileLoadingException {
        InputStream is = new FileInputStream(path);

        readFromInputStream(is);

        is.close();
    }

    private void readFromInputStream(InputStream inputStream) throws IOException, Utils.FileLoadingException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String strLine;

            while ((strLine = br.readLine()) != null) {
                String ss = strLine.trim();

                if (!ss.isEmpty()) {
                    String[] arr = ss.split("=");
                    if (arr.length <= 1) {
                        // It means, that options section is over
                        break;
                    } else if (arr.length > 2) {
                        throw new IOException("Config line is formatted incorrectly: " + strLine);
                    } else {
                        switch (arr[0].trim()) {
                            case "image":
                                image = Utils.loadBitmap(arr[1].trim());
                                break;
                            case "back":
                                background = Utils.loadBitmap(arr[1].trim());
                                break;
                            case "n":
                                int n = Integer.parseInt(arr[1].trim());
                                lines = new Stripe[n];
                                linesSorted = new Stripe[n];
                                break;
                            case "minDelta":
                               minDelta = Double.parseDouble(arr[1].trim());
                                break;
                            default:
                                throw new IOException("Unknown option line: " + strLine);
                        }
                    }
                }
            }

            if (image == null) {
                throw new IOException("No 'image' (picture to be drawn) option in config file!");
            }

            if(lines == null || linesSorted == null) {
                throw new IOException("No 'n' (number of lines) option in config file!");
            }

            if (strLine != null) {
                do {
                    strLine = strLine.trim();
                    if (!strLine.isEmpty()) {
                        loadLine(strLine);
                    }
                } while ((strLine = br.readLine()) != null);
            }

            stripeHeight = image.height / (double) lines.length;
        }

        // Fill missing stripes
        for (int index = 0; index < linesSorted.length; ++index) {
            if (linesSorted[index] == null) {
                Stripe s = new Stripe(index, minDelta, 255);
                linesSorted[index] = s;
                lines[counter++] = s;
            }
        }
    }

    private void loadLine(String line) throws IOException {
        try {
            String[] args = line.split(":");
            if (args.length != 2 && args.length != 3) throw new NumberFormatException();

            Stripe s = new Stripe(Integer.parseInt(args[0].trim()),
                    Math.max(Double.parseDouble(args[1].trim()) / 1000d, minDelta),
                    args.length == 3 ? Utils.border(Integer.parseInt(args[2].trim()), 0, 255) : 255);

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

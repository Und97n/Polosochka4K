package com.polosochka;

import java.io.*;
import java.util.ArrayList;

public class Configuration {

    public static ArrayList<Line> lines = new ArrayList<>();

    public static void readConfigFile(String path) throws IOException {
        InputStream is = Configuration.class.getClassLoader().getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException();
        }
        readFromInputStream(is);
    }

    private static void readFromInputStream(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            line = br.readLine();
            if (line == null) {
                throw new IOException("Config file is empty!");
            }
            try {
                int n = Integer.parseInt(line);
                lines = new ArrayList<>(n);
            } catch (NumberFormatException e) {
                throw new IOException("First config line is formatted incorrectly.");
            }

            while ((line = br.readLine()) != null) {
                lines.add(new Line(line));
            }
        }
    }

    static class Line {
        int pos;
        double delay;

        public Line(String line) throws IOException {
            try {
                String[] s = line.split(":");
                if (s.length != 2) throw new NumberFormatException();

                this.pos = Integer.parseInt(s[0]);
                this.delay = Double.parseDouble(s[1]) / 1000d;
            } catch (NumberFormatException e) {
                throw new IOException("Config line '" + line + "' is formatted incorrectly.");
            }
        }
    }

}

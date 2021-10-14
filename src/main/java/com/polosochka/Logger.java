package com.polosochka;

public class Logger {
    public static void write(String context, String msg, String logLevel) {
        System.out.println(logLevel + ": " + context + ": " + msg);
    }

    public static void warn(String context, String msg) {
        write(context, msg, "Warn");
    }

    public static void error(String context, String msg) {
        write(context, msg, "Error");
    }

    public static void writeException(String context, Throwable e, String level) {
        write(context, e.toString(), level);
        e.printStackTrace();
    }

    public static void writeException(String context, Throwable e) {
        writeException(context, e, "Debug");
    }

    public static void reportError(String context, String message, Throwable cause) {
        error(context, message);
        writeException(context, cause);
    }
}

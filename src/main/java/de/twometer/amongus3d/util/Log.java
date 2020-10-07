package de.twometer.amongus3d.util;

public class Log {

    private static void write(String prefix, String message) {
        System.out.printf("[%s] %s%n", prefix, message);
    }

    public static void d(String message) {
        write("DEBUG", message);
    }

    public static void i(String message) {
        write("INFO", message);
    }

    public static void w(String message) {
        write("WARN", message);
    }

    public static void e(String message) {
        write("ERROR", message);
    }

    public static void e(String message, Throwable t) {
        write("ERROR", message + ": " + t.toString());
    }

}

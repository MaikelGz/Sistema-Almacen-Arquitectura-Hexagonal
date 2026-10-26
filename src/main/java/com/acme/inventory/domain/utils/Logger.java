package com.acme.inventory.domain.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static boolean debugEnabled = false;
    private static boolean stacktraceEnabled = false;

    // ANSI color codes
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[34m";
    private static final String YELLOW = "\u001B[33m";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void setDebug(boolean enabled) {
        debugEnabled = enabled;
        info("Modo debug " + (enabled ? "activado" : "desactivado"));
    }

    public static void setStacktraceEnabled(boolean enabled) {
        stacktraceEnabled = enabled;
        info("Stacktrace " + (enabled ? "activado" : "desactivado"));
    }

    public static void info(String message) {
        System.out.println(formatMessage("INFO", message));
    }

    public static void debug(String message) {
        if (debugEnabled) {
            System.out.println(formatMessage("DEBUG", message));
        }
    }

    public static void error(String message, Throwable t) {
        System.out.println(formatMessage("ERROR", message));
        if (t != null) {
            System.out.println(RED + t.toString() + RESET);
            if (stacktraceEnabled) {
                for (StackTraceElement ste : t.getStackTrace()) {
                    System.out.println(RED + "\tat " + ste + RESET);
                }
            }
        }
    }

    private static String formatMessage(String level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String color;
        switch (level) {
            case "ERROR":
                color = RED;
                break;
            case "DEBUG":
                color = GREEN;
                break;
            case "INFO":
                color = BLUE;
                break;
            default:
                color = YELLOW;
        }
        return color + String.format("[%s] [%s] %s", timestamp, level, message) + RESET;
    }
}

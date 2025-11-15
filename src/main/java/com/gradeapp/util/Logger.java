package com.gradeapp.util;

import com.gradeapp.config.AppConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Simple logging utility for the application.
 * Provides console and file logging with different log levels.
 */
public class Logger {
    public enum Level {
        DEBUG, INFO, WARNING, ERROR
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static Logger instance;
    private Path logFile;
    private boolean enableFileLogging = true;
    private boolean enableConsoleLogging = true;
    private Level minLevel = Level.INFO;

    /**
     * Private constructor for singleton pattern.
     */
    private Logger() {
        try {
            Path appDir = AppConfig.getInstance().getAppDirectory();
            logFile = appDir.resolve("markbook.log");

            // Create log file if it doesn't exist
            if (!Files.exists(logFile)) {
                Files.createFile(logFile);
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
            enableFileLogging = false;
        }
    }

    /**
     * Gets the singleton instance of Logger.
     *
     * @return The Logger instance
     */
    public static synchronized Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    /**
     * Logs a debug message.
     *
     * @param message The message to log
     */
    public static void debug(String message) {
        getInstance().log(Level.DEBUG, message, null);
    }

    /**
     * Logs an info message.
     *
     * @param message The message to log
     */
    public static void info(String message) {
        getInstance().log(Level.INFO, message, null);
    }

    /**
     * Logs a warning message.
     *
     * @param message The message to log
     */
    public static void warning(String message) {
        getInstance().log(Level.WARNING, message, null);
    }

    /**
     * Logs a warning message with an exception.
     *
     * @param message The message to log
     * @param e The exception
     */
    public static void warning(String message, Exception e) {
        getInstance().log(Level.WARNING, message, e);
    }

    /**
     * Logs an error message.
     *
     * @param message The message to log
     */
    public static void error(String message) {
        getInstance().log(Level.ERROR, message, null);
    }

    /**
     * Logs an error message with an exception.
     *
     * @param message The message to log
     * @param e The exception
     */
    public static void error(String message, Exception e) {
        getInstance().log(Level.ERROR, message, e);
    }

    /**
     * Logs a message with the specified level.
     *
     * @param level The log level
     * @param message The message to log
     * @param exception Optional exception to log
     */
    private void log(Level level, String message, Exception exception) {
        // Check if this level should be logged
        if (level.ordinal() < minLevel.ordinal()) {
            return;
        }

        String timestamp = DATE_FORMAT.format(new Date());
        String logMessage = String.format("[%s] [%s] %s", timestamp, level, message);

        // Add exception details if present
        if (exception != null) {
            logMessage += "\n  Exception: " + exception.getClass().getSimpleName() + ": " + exception.getMessage();

            // Add stack trace for errors
            if (level == Level.ERROR && exception.getStackTrace().length > 0) {
                StackTraceElement[] trace = exception.getStackTrace();
                for (int i = 0; i < Math.min(5, trace.length); i++) {
                    logMessage += "\n    at " + trace[i].toString();
                }
            }
        }

        // Console logging
        if (enableConsoleLogging) {
            if (level == Level.ERROR) {
                System.err.println(logMessage);
            } else {
                System.out.println(logMessage);
            }
        }

        // File logging
        if (enableFileLogging && logFile != null) {
            try {
                Files.writeString(logFile, logMessage + "\n",
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                System.err.println("Failed to write to log file: " + e.getMessage());
            }
        }
    }

    /**
     * Sets the minimum log level.
     *
     * @param level The minimum level to log
     */
    public void setMinLevel(Level level) {
        this.minLevel = level;
    }

    /**
     * Enables or disables file logging.
     *
     * @param enabled true to enable, false to disable
     */
    public void setFileLogging(boolean enabled) {
        this.enableFileLogging = enabled;
    }

    /**
     * Enables or disables console logging.
     *
     * @param enabled true to enable, false to disable
     */
    public void setConsoleLogging(boolean enabled) {
        this.enableConsoleLogging = enabled;
    }

    /**
     * Gets the log file path.
     *
     * @return Path to the log file
     */
    public Path getLogFile() {
        return logFile;
    }

    /**
     * Clears the log file.
     */
    public void clearLog() {
        if (logFile != null) {
            try {
                Files.writeString(logFile, "");
                info("Log file cleared");
            } catch (IOException e) {
                error("Failed to clear log file", e);
            }
        }
    }
}

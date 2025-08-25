package it.fpili.imaginarium.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.*;

/**
 * Centralized logging configuration for Imaginarium.
 * Provides simple console output for users and detailed rotating log files for developers.
 */
public final class LoggerConfig {

    /** Thread-safe initialization flag (double-checked locking). */
    private static volatile boolean initialized = false;

    /** Private constructor to prevent instantiation. */
    private LoggerConfig() { }

    /**
     * Returns a logger for the given class.
     * Initializes configuration once with console and file handlers.
     * Console: INFO+ messages.
     * File: FINE+ messages with full details.
     *
     * @param cls the class requesting the logger
     * @return configured logger
     */
    public static Logger getLogger(Class<?> cls) {
        if (!initialized) {
            synchronized (LoggerConfig.class) {
                if (!initialized) {
                    init();
                }
            }
        }
        return Logger.getLogger(cls.getName());
    }

    /**
     * Sets up root logger with custom console and rotating file handlers.
     * Called once automatically when a logger is first requested.
     */
    private static void init() {
        Logger root = LogManager.getLogManager().getLogger("");
        for (Handler h : root.getHandlers()) {
            root.removeHandler(h);
        }
        root.setLevel(Level.INFO);
        installConsoleHandler(root);
        installFileHandler(root);
        initialized = true;
    }

    /**
     * Installs a console handler with simple, user-friendly messages.
     *
     * @param root the root logger
     */
    private static void installConsoleHandler(Logger root) {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        consoleHandler.setFormatter(new SafeConsoleFormatter());
        root.addHandler(consoleHandler);
    }

    /**
     * Installs a rotating file handler for detailed logs.
     * Logs stored under {@code logs/}, 1MB per file, 3 files kept.
     *
     * @param root the root logger
     */
    private static void installFileHandler(Logger root) {
        try {
            Path logDir = Path.of("logs");
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }
            FileHandler fileHandler = new FileHandler(
                    "logs/imaginarium-%u-%g.log",
                    1_000_000, 3, true
            );
            fileHandler.setEncoding("UTF-8");
            fileHandler.setLevel(Level.FINE);
            fileHandler.setFormatter(new SimpleFormatter());
            root.addHandler(fileHandler);
        } catch (IOException e) {
            root.log(Level.WARNING,
                    "File logging disabled: " + e.getMessage(), e);
        }
    }

    /**
     * Formatter for console output with only level and message.
     * Hides technical details like class, method, or timestamps.
     */
    private static class SafeConsoleFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return record.getLevel() + ": " + record.getMessage() + System.lineSeparator();
        }
    }
}
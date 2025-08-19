package it.fpili.imaginarium.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.*;

/**
 * Centralized logger configuration for the application.
 * <p>
 * Ensures that all loggers across the codebase share the same configuration:
 * <ul>
 *   <li>Removes default JVM log handlers (avoiding duplicate output).</li>
 *   <li>Sets the root logging level to {@link Level#INFO}.</li>
 *   <li>Adds a {@link ConsoleHandler} with {@link SimpleFormatter} for stdout logging.</li>
 *   <li>Adds a rotating {@link FileHandler} writing under {@code logs/}, with rollover
 *       every ~1 MB and up to 3 files kept.</li>
 * </ul>
 * Loggers are obtained on a per-class basis via {@link #getLogger(Class)}.
 * The initialization is performed lazily and thread-safely (double-checked locking).
 * </p>
 */
public final class LoggerConfig {
    private static volatile boolean initialized = false;

    /** Private constructor to prevent instantiation (utility class). */
    private LoggerConfig() {}

    /**
     * Returns a logger scoped to the given class, ensuring the logging
     * system is initialized exactly once.
     *
     * @param cls the class requesting a logger
     * @return configured logger instance
     */
    public static Logger getLogger(Class<?> cls) {
        if (!initialized) {
            synchronized (LoggerConfig.class) {
                if (!initialized) init();
            }
        }
        return Logger.getLogger(cls.getName());
    }

    /**
     * Initializes the logging configuration.
     * Removes existing handlers, installs console and file handlers.
     * <p>
     * If the file handler cannot be created (e.g., due to missing permissions),
     * logging will continue on the console only.
     * </p>
     */
    private static void init() {
        Logger root = LogManager.getLogManager().getLogger("");
        for (Handler h : root.getHandlers()) root.removeHandler(h);

        root.setLevel(Level.INFO);

        // Console handler
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.INFO);
        ch.setFormatter(new SimpleFormatter());
        root.addHandler(ch);

        // Rotating file handler (~1MB per file, up to 3 files, append mode)
        try {
            Path logDir = Path.of("logs");
            if (!Files.exists(logDir)) Files.createDirectories(logDir);

            FileHandler fh = new FileHandler("logs/imaginarium-%u-%g.log",
                    1_000_000, 3, true);
            fh.setEncoding("UTF-8");
            fh.setLevel(Level.FINE);
            fh.setFormatter(new SimpleFormatter());
            root.addHandler(fh);
        } catch (IOException e) {
            root.log(Level.WARNING, "File logging disabled: " + e.getMessage());
        }

        initialized = true;
    }
}


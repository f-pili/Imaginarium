package it.fpili.imaginarium.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.*;

/**
 * Centralized logger configuration for the application.
 * Provides a per-class logger and sets up console + rotating file handlers.
 */
public final class LoggerConfig {
    private static volatile boolean initialized = false;

    private LoggerConfig() {}

    /**
     * Returns a class-scoped logger, ensuring the logging system is initialized once.
     * @param cls the class requesting a logger
     * @return configured logger
     */
    public static Logger getLogger(Class<?> cls) {
        if (!initialized) {
            synchronized (LoggerConfig.class) {
                if (!initialized) init();
            }
        }
        return Logger.getLogger(cls.getName());
    }

    private static void init() {
        Logger root = LogManager.getLogManager().getLogger("");
        for (Handler h : root.getHandlers()) root.removeHandler(h);

        root.setLevel(Level.INFO);
        // Console
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.INFO);
        ch.setFormatter(new SimpleFormatter());
        root.addHandler(ch);

        // Rotating file handler
        try {
            Path logDir = Path.of("logs");
            if (!Files.exists(logDir)) Files.createDirectories(logDir);
            FileHandler fh = new FileHandler("logs/imaginarium-%u-%g.log", 1_000_000, 3, true);
            fh.setEncoding("UTF-8");
            fh.setLevel(Level.FINE);
            fh.setFormatter(new SimpleFormatter());
            root.addHandler(fh);
        } catch (IOException e) {
            // Last-resort: keep console logging only
            root.log(Level.WARNING, "File logging disabled: " + e.getMessage());
        }

        initialized = true;
    }
}

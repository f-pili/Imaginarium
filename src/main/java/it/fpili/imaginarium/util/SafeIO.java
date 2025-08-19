package it.fpili.imaginarium.util;

import it.fpili.imaginarium.exception.IoOperationException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/**
 * Minimal safe I/O helpers that wrap low-level exceptions into checked, controlled ones.
 * No secrets or absolute paths are hardcoded; callers provide paths.
 */
public final class SafeIO {
    private SafeIO() {}

    /**
     * Writes text to file using UTF-8, creating parent directories if needed.
     * @param path target path (relative recommended)
     * @param content text content
     * @throws IoOperationException wrapped IOException
     */
    public static void writeUtf8(Path path, String content) throws IoOperationException {
        try {
            Path parent = path.toAbsolutePath().getParent();
            if (parent != null) Files.createDirectories(parent);
            Files.writeString(path, content, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IoOperationException("Failed to write file", e);
        }
    }

    /**
     * Reads text from file using UTF-8.
     * @param path source path
     * @return file content
     * @throws IoOperationException wrapped IOException
     */
    public static String readUtf8(Path path) throws IoOperationException {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IoOperationException("Failed to read file", e);
        }
    }
}

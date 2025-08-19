package it.fpili.imaginarium.util;

import it.fpili.imaginarium.exception.IoOperationException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/**
 * Safe, minimal file I/O utilities.
 * <p>
 * This utility provides wrappers around {@link java.nio.file.Files} operations,
 * ensuring:
 * <ul>
 *   <li>UTF-8 is always used for text I/O (no platform default encoding issues).</li>
 *   <li>Low-level {@link IOException} are shielded and rethrown as
 *       checked {@link IoOperationException}, to avoid leaking technical details
 *       directly to higher layers.</li>
 *   <li>Parent directories are automatically created on write.</li>
 * </ul>
 * This class is final and cannot be instantiated (utility-only).
 * </p>
 */
public final class SafeIO {
    private SafeIO() {}

    /**
     * Writes text to a file using UTF-8 encoding.
     * Creates parent directories if they do not exist.
     *
     * @param path    target file path (relative paths recommended for portability)
     * @param content text content to write
     * @throws IoOperationException if any I/O error occurs
     */
    public static void writeUtf8(Path path, String content) throws IoOperationException {
        try {
            Path parent = path.toAbsolutePath().getParent();
            if (parent != null) Files.createDirectories(parent);
            Files.writeString(path, content, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IoOperationException("Failed to write file: " + path, e);
        }
    }

    /**
     * Reads text content from a file using UTF-8 encoding.
     *
     * @param path source file path
     * @return full file content as a string
     * @throws IoOperationException if any I/O error occurs
     */
    public static String readUtf8(Path path) throws IoOperationException {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IoOperationException("Failed to read file: " + path, e);
        }
    }
}


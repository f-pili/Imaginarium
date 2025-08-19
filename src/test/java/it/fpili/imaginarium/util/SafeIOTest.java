package it.fpili.imaginarium.util;

import it.fpili.imaginarium.exception.IoOperationException;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SafeIO} read/write helpers.
 * <p>
 * Scope:
 * <ul>
 *   <li>UTF-8 write-read roundtrip.</li>
 *   <li>Automatic parent directory creation on write.</li>
 *   <li>Wrapped exception on read of non-existent file.</li>
 * </ul>
 */
class SafeIOTest {

    @Test
    void writeThenReadUtf8() throws Exception {
        // Arrange
        Path tmp = Files.createTempDirectory("imaginarium-test").resolve("note.txt");
        String data = "Hello UTF-8 äèìòù";

        // Act
        SafeIO.writeUtf8(tmp, data);
        String back = SafeIO.readUtf8(tmp);

        // Assert
        assertEquals(data, back, "Content read back should match the written UTF-8 string");
    }

    @Test
    void writeCreatesParentDirectories() throws Exception {
        // Arrange: nested non-existing directory structure
        Path base = Files.createTempDirectory("imaginarium-test-deep");
        Path nested = base.resolve("a/b/c/notes.txt");
        String payload = "Deep write";

        // Act: should create parent dirs automatically
        SafeIO.writeUtf8(nested, payload);

        // Assert
        assertTrue(Files.exists(nested), "File should exist after writeUtf8");
        assertEquals(payload, SafeIO.readUtf8(nested), "Read content should match");
    }

    @Test
    void readNonExistingThrowsWrapped() {
        // Arrange
        Path nowhere = Path.of("definitely-not-existing-123.txt");

        // Act + Assert
        assertThrows(IoOperationException.class,
                () -> SafeIO.readUtf8(nowhere),
                "Reading a non-existent file should throw IoOperationException");
    }
}


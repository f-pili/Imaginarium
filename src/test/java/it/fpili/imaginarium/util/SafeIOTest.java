package it.fpili.imaginarium.util;

import it.fpili.imaginarium.exception.IoOperationException;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Minimal tests for SafeIO read/write.
 */
class SafeIOTest {

    @Test
    void writeThenReadUtf8() throws Exception {
        Path tmp = Files.createTempDirectory("imaginarium-test").resolve("note.txt");
        String data = "Hello UTF-8 äèìòù";
        SafeIO.writeUtf8(tmp, data);
        String back = SafeIO.readUtf8(tmp);
        assertEquals(data, back);
    }

    @Test
    void readNonExistingThrowsWrapped() {
        assertThrows(IoOperationException.class,
                () -> SafeIO.readUtf8(Path.of("definitely-not-existing-123.txt")));
    }
}

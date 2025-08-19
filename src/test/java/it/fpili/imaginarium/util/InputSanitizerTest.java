package it.fpili.imaginarium.util;

import it.fpili.imaginarium.exception.InputValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InputSanitizer.
 */
class InputSanitizerTest {

    @Test
    void trimsAndCollapsesWhitespace() throws Exception {
        String out = InputSanitizer.sanitizeLine("  hello   world  ", 50);
        assertEquals("hello world", out);
    }

    @Test
    void removesControlAndDisallowedChars() throws Exception {
        String out = InputSanitizer.sanitizeLine("A\u0007B <C> {D}", 50);
        assertEquals("AB C D", out);
    }

    @Test
    void enforcesMaxLen() {
        assertThrows(InputValidationException.class,
                () -> InputSanitizer.sanitizeLine("x".repeat(200), 10));
    }

    @Test
    void rejectsEmptyAfterSanitization() {
        assertThrows(InputValidationException.class,
                () -> InputSanitizer.sanitizeLine("\n\t  ", 10));
    }
}

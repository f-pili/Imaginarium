package it.fpili.imaginarium.util;

import it.fpili.imaginarium.exception.InputValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link InputSanitizer}.
 * <p>
 * Scope:
 * <ul>
 *   <li>Whitespace trimming and collapsing.</li>
 *   <li>Removal of control characters and disallowed symbols.</li>
 *   <li>Enforcement of maximum length.</li>
 *   <li>Handling of empty results after sanitization.</li>
 *   <li>Allowlisted punctuation retained correctly.</li>
 *   <li>Null handling (NPE) and boundary conditions.</li>
 * </ul>
 */
class InputSanitizerTest {

    @Test
    void trimsAndCollapsesWhitespace() throws Exception {
        // Arrange + Act
        String out = InputSanitizer.sanitizeLine("  hello   world  ", 50);

        // Assert
        assertEquals("hello world", out, "Multiple spaces should be collapsed and string trimmed");
    }

    @Test
    void removesControlAndDisallowedChars() throws Exception {
        // Arrange: includes a BEL control char and disallowed braces/angle brackets
        // Act
        String out = InputSanitizer.sanitizeLine("A\u0007B <C> {D}", 50);

        // Assert
        assertEquals("AB C D", out, "Control/disallowed symbols should be removed, spacing preserved");
    }

    @Test
    void enforcesMaxLen() {
        // Act + Assert
        assertThrows(InputValidationException.class,
                () -> InputSanitizer.sanitizeLine("x".repeat(200), 10),
                "Input longer than maxLen should be rejected");
    }

    @Test
    void rejectsEmptyAfterSanitization() {
        // Act + Assert
        assertThrows(InputValidationException.class,
                () -> InputSanitizer.sanitizeLine("\n\t  ", 10),
                "Input that becomes empty after sanitization should be rejected");
    }

    @Test
    void keepsAllowlistedPunctuation() throws Exception {
        // Arrange: only allowlisted symbols should survive
        String raw = "Alpha._-@#:/'+!?()&% Beta";
        // Act
        String out = InputSanitizer.sanitizeLine(raw, 200);
        // Assert
        assertEquals("Alpha._-@#:/'+!?()&% Beta", out,
                "Allowlisted punctuation must be preserved");
    }

    @Test
    void boundaryLengthEqualToMaxIsAccepted() throws Exception {
        // Arrange
        String raw = "A".repeat(20);
        // Act
        String out = InputSanitizer.sanitizeLine(raw, 20);
        // Assert
        assertEquals(raw, out, "String with length exactly maxLen should be accepted");
    }

    @Test
    void nullRawThrowsNpe() {
        // Act + Assert
        assertThrows(NullPointerException.class,
                () -> InputSanitizer.sanitizeLine(null, 10),
                "Null raw input should throw NPE per contract");
    }
}

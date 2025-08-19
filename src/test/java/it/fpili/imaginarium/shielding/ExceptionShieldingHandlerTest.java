package it.fpili.imaginarium.shielding;

import it.fpili.imaginarium.exception.ApplicationException;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ExceptionShieldingHandler}.
 * <p>
 * Verifies that:
 * <ul>
 *   <li>Low-level exceptions are logged and rethrown as {@link ApplicationException} with a safe message.</li>
 *   <li>A pre-existing {@link ApplicationException} is propagated unchanged (controlled propagation).</li>
 *   <li>The {@code void}-returning overload works equivalently.</li>
 * </ul>
 */
class ExceptionShieldingHandlerTest {

    @Test
    void shieldsRuntimeIntoApplicationException() {
        // Arrange
        var handler = new ExceptionShieldingHandler(Logger.getLogger("test"));

        // Act + Assert
        ApplicationException ex = assertThrows(ApplicationException.class, () ->
                        handler.guard(() -> { throw new RuntimeException("DB timeout details"); },
                                "Generic failure"),
                "A runtime exception should be shielded as ApplicationException");
        assertEquals("Generic failure", ex.getMessage(),
                "The user-facing message must be the safe, generic one");
    }

    @Test
    void propagatesExistingApplicationExceptionUnchanged() {
        // Arrange
        var handler = new ExceptionShieldingHandler(Logger.getLogger("test"));
        ApplicationException original = new ApplicationException("Already safe");

        // Act + Assert
        ApplicationException ex = assertThrows(ApplicationException.class, () ->
                handler.guard(() -> { throw original; }, "Should not be used"));
        assertSame(original, ex,
                "An existing ApplicationException should be propagated unchanged");
        assertEquals("Already safe", ex.getMessage(),
                "Original message must be preserved");
    }

    @Test
    void voidOverloadShieldsRuntimeException() {
        // Arrange
        var handler = new ExceptionShieldingHandler(Logger.getLogger("test"));

        // Act + Assert
        ApplicationException ex = assertThrows(ApplicationException.class, () ->
                        handler.guard(() -> { throw new IllegalStateException("io layer details"); },
                                "Operation failed"),
                "Void overload should also shield low-level exceptions");
        assertEquals("Operation failed", ex.getMessage(),
                "Safe message should be returned by the void overload as well");
    }
}


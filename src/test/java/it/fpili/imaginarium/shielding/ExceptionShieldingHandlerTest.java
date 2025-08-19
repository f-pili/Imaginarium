package it.fpili.imaginarium.shielding;

import it.fpili.imaginarium.exception.ApplicationException;
import org.junit.jupiter.api.Test;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionShieldingHandlerTest {
    @Test
    void shieldsRuntimeIntoApplicationException() {
        var handler = new ExceptionShieldingHandler(Logger.getLogger("test"));
        ApplicationException ex = assertThrows(ApplicationException.class, () ->
                handler.guard(() -> { throw new RuntimeException("DB timeout details"); },
                        "Generic failure"));
        assertEquals("Generic failure", ex.getMessage());
    }
}

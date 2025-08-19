package it.fpili.imaginarium.shielding;

import it.fpili.imaginarium.exception.ApplicationException;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralized exception shielding: logs internal details and exposes safe messages.
 */
public final class ExceptionShieldingHandler {
    @FunctionalInterface public interface RunnableX { void run() throws Exception; }

    private final Logger log;
    public ExceptionShieldingHandler(Logger log) { this.log = log; }

    public <T> T guard(Callable<T> action, String userMsg) throws ApplicationException {
        try {
            return action.call();
        } catch (ApplicationException ae) {            // already safe, just log at WARNING
            log.log(Level.WARNING, "Application error", ae);
            throw ae;
        } catch (Exception e) {                        // shield low-level exception
            log.log(Level.SEVERE, "Internal error", e);
            throw new ApplicationException(userMsg);
        }
    }

    public void guard(RunnableX action, String userMsg) throws ApplicationException {
        guard(() -> { action.run(); return null; }, userMsg);
    }
}

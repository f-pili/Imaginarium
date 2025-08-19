package it.fpili.imaginarium.shielding;

import it.fpili.imaginarium.exception.ApplicationException;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralized Exception Shielding helper.
 * <p>
 * Wraps execution of application actions, logging internal exceptions with details,
 * and rethrowing a safe {@link ApplicationException} with a user-facing message.
 * If an {@code ApplicationException} is already thrown by the action, it is logged at
 * {@link Level#WARNING} and propagated unchanged (controlled propagation).
 * </p>
 */
public final class ExceptionShieldingHandler {

    /**
     * Functional interface similar to {@link Runnable} but allowing checked exceptions.
     */
    @FunctionalInterface
    public interface RunnableX {
        /**
         * Executes an action that may throw a checked exception.
         *
         * @throws Exception any exception thrown by the action
         */
        void run() throws Exception;
    }

    private final Logger log;

    /**
     * Creates a shielding handler that logs via the provided {@link Logger}.
     *
     * @param log logger to use (must not be {@code null})
     * @throws NullPointerException if {@code log} is {@code null}
     */
    public ExceptionShieldingHandler(Logger log) {
        this.log = Objects.requireNonNull(log, "log must not be null");
    }

    /**
     * Guards a callable action and shields low-level exceptions.
     * <ul>
     *   <li>If the action completes, its result is returned.</li>
     *   <li>If it throws {@link ApplicationException}, the exception is logged at WARNING and rethrown.</li>
     *   <li>If it throws any other exception, details are logged at SEVERE and a new
     *       {@link ApplicationException} with {@code userMsg} is thrown.</li>
     * </ul>
     *
     * @param action  the action to execute
     * @param userMsg safe, user-facing message for generic failures
     * @param <T>     return type of the action
     * @return the action result
     * @throws ApplicationException if the action fails (shielded or propagated)
     */
    public <T> T guard(Callable<T> action, String userMsg) throws ApplicationException {
        try {
            return action.call();
        } catch (ApplicationException ae) { // already safe, just log at WARNING
            log.log(Level.WARNING, "Application error", ae);
            throw ae;
        } catch (Exception e) {             // shield low-level exception
            log.log(Level.SEVERE, "Internal error", e);
            throw new ApplicationException(userMsg);
        }
    }

    /**
     * Guards a void-returning action and shields low-level exceptions.
     * <p>
     * Convenience overload that delegates to {@link #guard(Callable, String)}.
     * </p>
     *
     * @param action  the action to execute
     * @param userMsg safe, user-facing message for generic failures
     * @throws ApplicationException if the action fails (shielded or propagated)
     */
    public void guard(RunnableX action, String userMsg) throws ApplicationException {
        guard(() -> {
            action.run();
            return null;
        }, userMsg);
    }
}


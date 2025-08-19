package it.fpili.imaginarium.exception;

/**
 * Base checked exception for application-level failures.
 * <p>
 * This exception is intended to signal predictable issues
 * in the business or persistence layer that should be
 * reported to the user in a safe and controlled way,
 * without exposing sensitive technical details.
 * </p>
 */
public class ApplicationException extends Exception {

    /**
     * Creates a new {@code ApplicationException} with the given message.
     *
     * @param message description of the failure
     */
    public ApplicationException(String message) {
        super(message);
    }

    /**
     * Creates a new {@code ApplicationException} with the given message and cause.
     *
     * @param message description of the failure
     * @param cause   underlying cause of this exception (may be null)
     */
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
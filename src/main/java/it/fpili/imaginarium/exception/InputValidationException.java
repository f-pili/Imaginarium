package it.fpili.imaginarium.exception;

/**
 * Exception thrown when user-provided input does not satisfy
 * validation constraints such as length, allowed characters,
 * or required fields.
 * <p>
 * This is a checked exception and should be caught explicitly
 * where input is collected and sanitized.
 * </p>
 */
public class InputValidationException extends ApplicationException {

    /**
     * Creates a new {@code InputValidationException} with the given message.
     *
     * @param message description of the validation failure
     */
    public InputValidationException(String message) {
        super(message);
    }
}


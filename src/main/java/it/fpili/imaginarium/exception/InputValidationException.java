package it.fpili.imaginarium.exception;

/**
 * Thrown when user input does not satisfy validation constraints.
 */
public class InputValidationException extends ApplicationException {
    public InputValidationException(String message) { super(message); }
}

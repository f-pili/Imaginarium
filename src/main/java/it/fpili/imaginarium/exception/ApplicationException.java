package it.fpili.imaginarium.exception;

/**
 * Base checked exception for application-level failures that should be reported
 * without leaking sensitive technical details (controlled propagation).
 */
public class ApplicationException extends Exception {
    public ApplicationException(String message) { super(message); }
    public ApplicationException(String message, Throwable cause) { super(message, cause); }
}

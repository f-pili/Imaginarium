package it.fpili.imaginarium.exception;

/**
 * Wraps I/O layer issues to avoid leaking low-level exceptions upstream.
 */
public class IoOperationException extends ApplicationException {
    public IoOperationException(String message, Throwable cause) { super(message, cause); }
}

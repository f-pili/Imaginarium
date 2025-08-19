package it.fpili.imaginarium.exception;

/**
 * Exception wrapping failures in the I/O layer such as
 * file access, read/write errors, or path issues.
 * <p>
 * It prevents leaking low-level {@link java.io.IOException}
 * or similar technical details to higher application layers,
 * ensuring controlled propagation through {@link ApplicationException}.
 * </p>
 */
public class IoOperationException extends ApplicationException {

    /**
     * Creates a new {@code IoOperationException} with a message
     * and the original cause.
     *
     * @param message description of the I/O failure
     * @param cause   the underlying exception to wrap
     */
    public IoOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}


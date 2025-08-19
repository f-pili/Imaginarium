package it.fpili.imaginarium.util;

import it.fpili.imaginarium.exception.InputValidationException;

import java.util.Objects;

/**
 * Utility for sanitizing and validating user-provided strings.
 * Policy: keep letters/digits/whitespace and a small safe punctuation set,
 * collapse whitespace, trim, and enforce max length.
 */
public final class InputSanitizer {
    private InputSanitizer() {}

    /**
     * Sanitizes a free-text line according to a conservative policy.
     * @param raw user-provided text
     * @param maxLen maximum accepted length after sanitization
     * @return sanitized string
     * @throws InputValidationException if result is empty or exceeds length
     */
    public static String sanitizeLine(String raw, int maxLen) throws InputValidationException {
        Objects.requireNonNull(raw, "raw");
        String s = raw
                .replaceAll("\\p{Cntrl}", "")                  // remove control chars
                .replaceAll("[^\\p{L}\\p{N}\\s.,_\\-@#:/'+!?()&%]", "") // allowlist
                .replaceAll("\\s+", " ")                         // collapse spaces
                .trim();
        if (s.isEmpty()) throw new InputValidationException("Input cannot be empty");
        if (s.length() > maxLen) throw new InputValidationException("Input too long (max " + maxLen + ")");
        return s;
    }
}

package it.fpili.imaginarium.util;

import it.fpili.imaginarium.exception.InputValidationException;

import java.util.Objects;

/**
 * Utility class for sanitizing and validating user-provided strings.
 * <p>
 * The sanitization policy is conservative:
 * <ul>
 *   <li>Removes control characters.</li>
 *   <li>Allowlist: keeps only letters, digits, whitespace, and a limited punctuation set
 *       ({@code . , _ - @ # : / ' + ! ? ( ) & %}).</li>
 *   <li>Collapses multiple spaces into a single space.</li>
 *   <li>Trims leading and trailing whitespace.</li>
 * </ul>
 * After sanitization, the result must be non-empty and shorter than the maximum length.
 * </p>
 */
public final class InputSanitizer {

    /** Private constructor to prevent instantiation (utility class). */
    private InputSanitizer() {}

    /**
     * Sanitizes a free-text line according to a conservative allowlist policy.
     *
     * @param raw    user-provided text (non-null)
     * @param maxLen maximum accepted length after sanitization
     * @return sanitized string
     * @throws NullPointerException      if {@code raw} is {@code null}
     * @throws InputValidationException  if the sanitized string is empty or exceeds {@code maxLen}
     */
    public static String sanitizeLine(String raw, int maxLen) throws InputValidationException {
        Objects.requireNonNull(raw, "raw");

        String s = raw
                .replaceAll("\\p{Cntrl}", "")   // remove control characters (ASCII 0–31 and DEL)
                .replaceAll(
                        "[^\\p{L}\\p{N}\\s.,_\\-@#:/'+!?()&%]",
                        ""                      // allow only letters, digits, space, and safe punctuation
                )
                .replaceAll("\\s+", " ")        // collapse multiple spaces/tabs into one space
                .trim();

        if (s.isEmpty()) {
            throw new InputValidationException("Input cannot be empty");
        }
        if (s.length() > maxLen) {
            throw new InputValidationException("Input too long (max " + maxLen + ")");
        }

        return s;
    }
}
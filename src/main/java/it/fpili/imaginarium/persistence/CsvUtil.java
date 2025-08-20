package it.fpili.imaginarium.persistence;

/**
 * Small CSV escaping and parsing utilities (RFC4180-ish).
 * <p>
 * This class supports quoting with {@code "}, escaping of inner quotes
 * by doubling them ({@code ""}), and detection of fields containing
 * commas or newlines that must be quoted.
 * It is intentionally simplified for educational purposes.
 * </p>
 */
final class CsvUtil {

    private CsvUtil() {
        // Utility class; prevent instantiation.
    }

    /**
     * Escapes a single field value for safe CSV writing.
     * <ul>
     *     <li>Null values become empty strings.</li>
     *     <li>Quotes inside the value are doubled (e.g. {@code "} → {@code ""}).</li>
     *     <li>If the value contains commas, quotes, or newlines, the entire field is quoted.</li>
     * </ul>
     *
     * @param s the raw field value (possibly null)
     * @return the escaped value suitable for inclusion in a CSV line
     */
    static String esc(String s) {
        if (s == null) return "";
        boolean needQuotes = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String v = s.replace("\"", "\"\"");
        return needQuotes ? "\"" + v + "\"" : v;
    }

    /**
     * Parses a CSV line into fields.
     * <p>
     * Supports:
     * <ul>
     *     <li>Quoted fields with {@code "value"}.</li>
     *     <li>Escaped quotes inside fields represented as {@code ""}.</li>
     *     <li>Unquoted fields separated by commas.</li>
     * </ul>
     * Does not support multiline fields (line breaks inside quotes are ignored for simplicity).
     * </p>
     *
     * @param line the raw CSV line
     * @return an array of parsed values (never null, may be empty)
     */
    static String[] parseLine(String line) {
        // Simple parser adequate for 4-column student exercise
        StringBuilder cur = new StringBuilder();
        boolean inQ = false;
        java.util.List<String> out = new java.util.ArrayList<>(4);

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQ) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        // Escaped quote ("")
                        cur.append('"');
                        i++;
                    } else {
                        // End of quoted section
                        inQ = false;
                    }
                } else {
                    cur.append(c);
                }
            } else {
                if (c == '"') {
                    inQ = true;
                } else if (c == ',') {
                    out.add(cur.toString());
                    cur.setLength(0);
                } else {
                    cur.append(c);
                }
            }
        }
        out.add(cur.toString());
        return out.toArray(String[]::new);
    }
}
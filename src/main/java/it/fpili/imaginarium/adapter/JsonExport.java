package it.fpili.imaginarium.adapter;

/**
 * Target interface expected by modern clients wanting JSON export.
 */
public interface JsonExport {
    /**
     * Returns the JSON representation of the adapted data.
     * @return JSON string
     * @throws Exception if export fails
     */
    String toJson() throws Exception;
}
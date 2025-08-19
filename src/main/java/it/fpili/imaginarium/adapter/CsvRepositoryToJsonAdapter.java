package it.fpili.imaginarium.adapter;

import it.fpili.imaginarium.exception.ApplicationException;
import it.fpili.imaginarium.model.Item;
import it.fpili.imaginarium.persistence.Repository;

import java.util.List;
import java.util.Objects;

/**
 * Adapter that bridges a CSV-backed {@link Repository} (adaptee) to a JSON export interface (target).
 * <p>
 * Rationale: clients expect JSON while the existing persistence is CSV. This class converts the
 * repository data to a JSON string without changing the repository implementation or leaking
 * low-level details to callers.
 * </p>
 * <p>
 * Note: To keep the project dependency-free for a first-year student scope, JSON is built
 * manually with basic string escaping; no external JSON libraries are used.
 * </p>
 */
public final class CsvRepositoryToJsonAdapter implements JsonExport {

    /** The adaptee being wrapped. */
    private final Repository<Item, String> repo;

    /**
     * Creates the adapter around a repository.
     *
     * @param repo the item repository to adapt (must not be {@code null})
     * @throws NullPointerException if {@code repo} is {@code null}
     */
    public CsvRepositoryToJsonAdapter(Repository<Item, String> repo) {
        this.repo = Objects.requireNonNull(repo, "repo");
    }

    /**
     * Produces a JSON representation of the catalog in the form:
     * <pre>{@code {"data":[{...},{...}]}}</pre>
     *
     * @return a JSON string containing all items
     * @throws ApplicationException if fetching items from the repository fails
     */
    @Override
    public String toJson() throws ApplicationException {
        List<Item> items = repo.findAll();

        StringBuilder sb = new StringBuilder();
        sb.append("{\"data\":[");
        for (int i = 0; i < items.size(); i++) {
            Item it = items.get(i);
            sb.append("{")
                    .append("\"id\":\"").append(esc(it.id())).append("\",")
                    .append("\"name\":\"").append(esc(it.name())).append("\",")
                    .append("\"category\":\"").append(esc(it.category())).append("\",")
                    .append("\"description\":\"").append(esc(it.description())).append("\"")
                    .append("}");
            if (i < items.size() - 1) sb.append(",");
        }
        sb.append("]}");
        return sb.toString();
    }

    /**
     * Minimal JSON string escaper for a small subset of characters.
     * Keeps the implementation simple and adequate for this project.
     *
     * @param s input string (may be {@code null})
     * @return escaped string suitable for inclusion in JSON string literals
     */
    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}


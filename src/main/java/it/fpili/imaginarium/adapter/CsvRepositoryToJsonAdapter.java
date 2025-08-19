package it.fpili.imaginarium.adapter;

import it.fpili.imaginarium.exception.ApplicationException;
import it.fpili.imaginarium.model.Item;
import it.fpili.imaginarium.persistence.Repository;

import java.util.List;
import java.util.Objects;

/**
 * Adapter: wraps a Repository<Item,String> (adaptee) and exposes JSON export (target).
 * No external JSON libs to keep it student-level and dependency-free.
 */
public final class CsvRepositoryToJsonAdapter implements JsonExport {
    private final Repository<Item, String> repo;

    public CsvRepositoryToJsonAdapter(Repository<Item, String> repo) {
        this.repo = Objects.requireNonNull(repo);
    }

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

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r");
    }
}

package it.fpili.imaginarium.persistence;

import it.fpili.imaginarium.exception.IoOperationException;
import it.fpili.imaginarium.model.Item;
import it.fpili.imaginarium.util.LoggerConfig;
import it.fpili.imaginarium.util.SafeIO;
import it.fpili.imaginarium.factory.CsvItemCreator;

import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

/**
 * CSV-backed repository for Item. File format: id,name,category,description
 * In-memory index maintained on load/save for O(1) lookups.
 */
public final class CsvItemRepository implements Repository<Item, String> {
    private static final Logger log = LoggerConfig.getLogger(CsvItemRepository.class);
    private final Path file;
    private final Map<String, Item> index = new LinkedHashMap<>();

    /**
     * Creates a repository bound to a CSV file path.
     * @param csvPath path to CSV file (will be created on first save)
     */
    public CsvItemRepository(Path csvPath) {
        this.file = Objects.requireNonNull(csvPath, "csvPath");
        loadQuietly();
    }

    @Override
    public synchronized void save(Item entity) throws IoOperationException {
        index.put(entity.id(), entity);
        persist();
    }

    @Override
    public synchronized Optional<Item> findById(String id) {
        return Optional.ofNullable(index.get(id));
    }

    @Override
    public synchronized List<Item> findAll() {
        return List.copyOf(index.values());
    }

    @Override
    public synchronized void deleteById(String id) throws IoOperationException {
        index.remove(id);
        persist();
    }

    private void loadQuietly() {
        try {
            if (!java.nio.file.Files.exists(file)) return;
            String content = SafeIO.readUtf8(file);
            index.clear();
            for (String line : content.split("\\R")) {
                if (line.isBlank()) continue;
                if (line.regionMatches(true, 0, "ID,Name,Category,Description", 0, 28)) continue; // skip header
                String[] cols = CsvUtil.parseLine(line);
                if (cols.length < 4) { log.warning("Skipping malformed line: " + line); continue; }
                Item it = new CsvItemCreator(cols).build();
                index.put(it.id(), it);
            }
            log.info("Loaded " + index.size() + " items from CSV");
        } catch (IoOperationException e) {
            log.warning("Unable to load CSV: " + e.getMessage());
        }
    }

    private void persist() throws IoOperationException {
        StringBuilder sb = new StringBuilder();

        // Always write header first
        sb.append("ID,Name,Category,Description\n");

        for (Item it : index.values()) {
            sb.append(CsvUtil.esc(it.id())).append(',')
                    .append(CsvUtil.esc(it.name())).append(',')
                    .append(CsvUtil.esc(it.category())).append(',')
                    .append(CsvUtil.esc(it.description())).append('\n');
        }
        SafeIO.writeUtf8(file, sb.toString());
        log.fine("CSV persisted: " + index.size() + " items (with header)");
    }
}

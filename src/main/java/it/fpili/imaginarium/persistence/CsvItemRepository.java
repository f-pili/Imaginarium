package it.fpili.imaginarium.persistence;

import it.fpili.imaginarium.exception.ApplicationException;
import it.fpili.imaginarium.exception.IoOperationException;
import it.fpili.imaginarium.model.Item;
import it.fpili.imaginarium.util.LoggerConfig;
import it.fpili.imaginarium.util.SafeIO;
import it.fpili.imaginarium.factory.CsvItemCreator;

import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

/**
 * Repository backed by a CSV file for storing {@link Item} entities.
 * <p>
 * File format: {@code ID,Name,Category,Description}
 * The first line is always a header, automatically written on persist.
 * Data is cached in an in-memory map for efficient lookups.
 * </p>
 */
public final class CsvItemRepository implements Repository<Item, String> {
    private static final Logger log = LoggerConfig.getLogger(CsvItemRepository.class);
    private final Path file;
    private final Map<String, Item> index = new LinkedHashMap<>();

    /**
     * Creates a repository bound to a specific CSV file path.
     * The file is loaded immediately if it exists.
     *
     * @param csvPath path to the CSV file (created automatically on first save if missing)
     */
    public CsvItemRepository(Path csvPath) {
        this.file = Objects.requireNonNull(csvPath, "csvPath");
        loadQuietly();
    }

    /**
     * Saves or updates an item in the repository and persists changes to disk.
     *
     * @param entity the item to save
     * @throws IoOperationException if the write operation fails
     */
    @Override
    public synchronized void save(Item entity) throws IoOperationException {
        index.put(entity.id(), entity);
        persist();
    }

    /**
     * Finds an item by its unique ID.
     *
     * @param id the item identifier
     * @return an {@link Optional} containing the item if present, otherwise empty
     */
    @Override
    public synchronized Optional<Item> findById(String id) {
        return Optional.ofNullable(index.get(id));
    }

    /**
     * Retrieves all items currently stored in the repository.
     *
     * @return immutable list of items (never null)
     */
    @Override
    public synchronized List<Item> findAll() {
        return List.copyOf(index.values());
    }

    /**
     * Deletes an item by its unique identifier from the repository.
     * Throws exception if item does not exist, ensuring clear feedback for non-existent deletions.
     * Updates both in-memory index and persistent CSV storage.
     *
     * @param id the unique identifier of the item to delete
     * @throws ApplicationException if no item exists with the specified ID, or if persistence fails
     */

    @Override
    public synchronized void deleteById(String id) throws ApplicationException {
        if (!index.containsKey(id)) {
            throw new ApplicationException("Item with ID '" + id + "' not found");
        }
        index.remove(id);
        persist();
        log.fine("Successfully deleted item with id=" + id);
    }

    /**
     * Loads items from the CSV file into memory.
     * Skips header and malformed lines.
     * Logs warnings on load failure instead of throwing.
     */
    private void loadQuietly() {
        try {
            if (!java.nio.file.Files.exists(file)) return;
            String content = SafeIO.readUtf8(file);
            index.clear();
            for (String line : content.split("\\R")) {
                if (line.isBlank()) continue;
                if (line.regionMatches(true, 0, "ID,Name,Category,Description", 0, 28)) continue; // skip header
                String[] cols = CsvUtil.parseLine(line);
                if (cols.length < 4) {
                    log.warning("Skipping malformed line: " + line);
                    continue;
                }
                Item it = new CsvItemCreator(cols).build();
                index.put(it.id(), it);
            }
            log.info("Loaded " + index.size() + " items from CSV");
        } catch (IoOperationException e) {
            log.warning("Unable to load CSV: " + e.getMessage());
        }
    }

    /**
     * Persists the in-memory index to the CSV file,
     * always writing a header line first.
     *
     * @throws IoOperationException if the write operation fails
     */
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
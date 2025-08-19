package it.fpili.imaginarium.adapter;

import it.fpili.imaginarium.exception.ApplicationException;
import it.fpili.imaginarium.model.Item;
import it.fpili.imaginarium.persistence.CsvItemRepository;
import it.fpili.imaginarium.util.SafeIO;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration-style tests for the CSV → JSON Adapter.
 * <p>
 * Scope:
 * <ul>
 *   <li>End-to-end export using a real {@link CsvItemRepository}.</li>
 *   <li>Header handling in CSV (ignored while reading).</li>
 *   <li>Basic JSON string escaping rules (quotes and newlines).</li>
 *   <li>File I/O roundtrip via {@link SafeIO}.</li>
 *   <li>Propagation of {@link ApplicationException} when the adaptee fails.</li>
 * </ul>
 * Rationale: keeps the adapter dependency-free (manual JSON building) but verifies correctness.
 */
class JsonAdapterTest {

    /**
     * Verifies that items saved to a CSV repository can be exported to JSON
     * and that the CSV header line is correctly ignored when loading.
     */
    @Test
    void exportsCatalogAsJsonWithHeaderIgnored() throws Exception {
        // Arrange
        Path dir = Files.createTempDirectory("imag-json");
        Path csv = dir.resolve("items.csv");
        CsvItemRepository repo = new CsvItemRepository(csv);
        repo.save(new Item("id-1", "Sky Spoon", "Tools", "Scoops clouds"));
        repo.save(new Item("id-2", "Echo Jar", "Containers", "Stores echoes"));

        // Act
        CsvRepositoryToJsonAdapter adapter = new CsvRepositoryToJsonAdapter(repo);
        String json = adapter.toJson();

        // Assert
        assertTrue(json.startsWith("{\"data\":["),
                "JSON should start with an array container under 'data'");
        assertTrue(json.contains("\"id\":\"id-1\""), "Export should include id-1");
        assertTrue(json.contains("\"name\":\"Sky Spoon\""), "Export should include item name");
        assertTrue(json.contains("\"category\":\"Tools\""), "Export should include item category");
        assertTrue(json.endsWith("]}"), "JSON should close the 'data' array and root object");
    }

    /**
     * Verifies escaping of quotes and newlines in JSON string values.
     */
    @Test
    void escapesQuotesAndNewlines() throws Exception {
        // Arrange
        Path dir = Files.createTempDirectory("imag-json-esc");
        Path csv = dir.resolve("items.csv");
        CsvItemRepository repo = new CsvItemRepository(csv);
        repo.save(new Item("id-3", "Quote \"Name\"", "Cat", "Line1\nLine2"));

        // Act
        String json = new CsvRepositoryToJsonAdapter(repo).toJson();

        // Assert
        assertTrue(json.contains("\"name\":\"Quote \\\"Name\\\"\""),
                "Double quote in name must be escaped as \\\"");
        assertTrue(json.contains("\"description\":\"Line1\\nLine2\""),
                "Newline must be escaped as \\n inside JSON strings");
    }

    /**
     * Verifies that the produced JSON can be written and read back verbatim using {@link SafeIO}.
     */
    @Test
    void writesJsonFileViaSafeIO() throws Exception {
        // Arrange
        Path dir = Files.createTempDirectory("imag-json-file");
        Path csv = dir.resolve("items.csv");
        Path out = dir.resolve("items.json");
        CsvItemRepository repo = new CsvItemRepository(csv);
        repo.save(new Item("id-9", "Test", "Cat", "Desc"));

        // Act
        String json = new CsvRepositoryToJsonAdapter(repo).toJson();
        SafeIO.writeUtf8(out, json);
        String back = SafeIO.readUtf8(out);

        // Assert
        assertEquals(json, back, "JSON written and read back should match exactly");
    }

    /**
     * Ensures that failures in the adaptee (repository) are propagated as {@link ApplicationException}.
     */
    @Test
    void adapterPropagatesAsApplicationException() {
        // Arrange: a fake repository that fails on findAll()
        CsvRepositoryToJsonAdapter bad = new CsvRepositoryToJsonAdapter(new it.fpili.imaginarium.persistence.Repository<>() {
            public void save(Item e) { /* no-op */ }
            public java.util.Optional<Item> findById(String id) { return java.util.Optional.empty(); }
            public java.util.List<Item> findAll() throws ApplicationException { throw new ApplicationException("repo failure"); }
            public void deleteById(String id) { /* no-op */ }
        });

        // Act + Assert
        assertThrows(ApplicationException.class, bad::toJson,
                "Adapter should rethrow ApplicationException when the adaptee fails");
    }
}

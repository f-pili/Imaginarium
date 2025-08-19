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
 * Integration-style tests for CSV->JSON adapter.
 */
class JsonAdapterTest {

    @Test
    void exportsCatalogAsJsonWithHeaderIgnored() throws Exception {
        Path dir = Files.createTempDirectory("imag-json");
        Path csv = dir.resolve("items.csv");
        CsvItemRepository repo = new CsvItemRepository(csv);

        repo.save(new Item("id-1", "Sky Spoon", "Tools", "Scoops clouds"));
        repo.save(new Item("id-2", "Echo Jar", "Containers", "Stores echoes"));

        CsvRepositoryToJsonAdapter adapter = new CsvRepositoryToJsonAdapter(repo);
        String json = adapter.toJson();

        assertTrue(json.startsWith("{\"data\":["));
        assertTrue(json.contains("\"id\":\"id-1\""));
        assertTrue(json.contains("\"name\":\"Sky Spoon\""));
        assertTrue(json.contains("\"category\":\"Tools\""));
        assertTrue(json.endsWith("]}"));
    }

    @Test
    void escapesQuotesAndNewlines() throws Exception {
        Path dir = Files.createTempDirectory("imag-json-esc");
        Path csv = dir.resolve("items.csv");
        CsvItemRepository repo = new CsvItemRepository(csv);

        repo.save(new Item("id-3", "Quote \"Name\"", "Cat", "Line1\nLine2"));

        String json = new CsvRepositoryToJsonAdapter(repo).toJson();

        assertTrue(json.contains("\"name\":\"Quote \\\"Name\\\"\""));
        assertTrue(json.contains("\"description\":\"Line1\\nLine2\""));
    }

    @Test
    void writesJsonFileViaSafeIO() throws Exception {
        Path dir = Files.createTempDirectory("imag-json-file");
        Path csv = dir.resolve("items.csv");
        Path out = dir.resolve("items.json");
        CsvItemRepository repo = new CsvItemRepository(csv);

        repo.save(new Item("id-9", "Test", "Cat", "Desc"));

        String json = new CsvRepositoryToJsonAdapter(repo).toJson();
        SafeIO.writeUtf8(out, json);

        String back = SafeIO.readUtf8(out);
        assertEquals(json, back);
    }

    @Test
    void adapterPropagatesAsApplicationException() {
        CsvRepositoryToJsonAdapter bad = new CsvRepositoryToJsonAdapter(new it.fpili.imaginarium.persistence.Repository<>() {
            public void save(Item e) { }
            public java.util.Optional<Item> findById(String id){ return java.util.Optional.empty(); }
            public java.util.List<Item> findAll() throws ApplicationException { throw new ApplicationException("repo failure"); }
            public void deleteById(String id){ }
        });
        assertThrows(ApplicationException.class, bad::toJson);
    }
}
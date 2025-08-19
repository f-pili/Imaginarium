package it.fpili.imaginarium.persistence;

import it.fpili.imaginarium.model.Item;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Round-trip tests for CsvItemRepository.
 */
class CsvItemRepositoryTest {

    @Test
    void saveFindAndReload() throws Exception {
        Path dir = Files.createTempDirectory("imag-csv");
        Path file = dir.resolve("items.csv");
        CsvItemRepository repo = new CsvItemRepository(file);

        Item a = new Item("id-1","Sky Spoon","Tools","A spoon that scoops clouds");
        Item b = new Item("id-2","Echo Jar","Containers","It stores echoes for later");
        repo.save(a);
        repo.save(b);

        assertTrue(repo.findById("id-1").isPresent());
        assertEquals(2, repo.findAll().size());

        CsvItemRepository repo2 = new CsvItemRepository(file);
        assertEquals(2, repo2.findAll().size());
        assertEquals("Echo Jar", repo2.findById("id-2").orElseThrow().name());
    }
}

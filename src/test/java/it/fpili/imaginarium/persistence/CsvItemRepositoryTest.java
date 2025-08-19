package it.fpili.imaginarium.persistence;

import it.fpili.imaginarium.model.Item;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration-like tests for {@link CsvItemRepository},
 * verifying persistence to and from a real CSV file.
 */
class CsvItemRepositoryTest {

    /**
     * Ensures that items saved to the repository can be found,
     * and that reloading from disk restores them correctly.
     */
    @Test
    void saveFindReloadWorks() throws Exception {
        Path dir = Files.createTempDirectory("imag-csv");
        Path file = dir.resolve("items.csv");
        CsvItemRepository repo = new CsvItemRepository(file);

        Item a = new Item("id-1","Sky Spoon","Tools","A spoon that scoops clouds");
        Item b = new Item("id-2","Echo Jar","Containers","It stores echoes for later");
        repo.save(a);
        repo.save(b);

        // In-memory checks
        assertTrue(repo.findById("id-1").isPresent(), "Item with id-1 should be found");
        assertEquals(2, repo.findAll().size(), "Repo should contain 2 items");

        // Reload from file
        CsvItemRepository repo2 = new CsvItemRepository(file);
        assertEquals(2, repo2.findAll().size(), "Reloaded repo should contain 2 items");
        assertEquals("Echo Jar", repo2.findById("id-2").orElseThrow().name(),
                "Reloaded item with id-2 should be Echo Jar");
    }

    /**
     * Ensures that deleting an item removes it from both memory and persisted CSV.
     */
    @Test
    void deleteItemRemovesFromRepoAndFile() throws Exception {
        Path dir = Files.createTempDirectory("imag-csv-del");
        Path file = dir.resolve("items.csv");
        CsvItemRepository repo = new CsvItemRepository(file);

        repo.save(new Item("id-3","Dream Compass","Tools","Points to your dreams"));

        assertTrue(repo.findById("id-3").isPresent(), "Item should be present before delete");

        repo.deleteById("id-3");

        assertFalse(repo.findById("id-3").isPresent(), "Item should be removed after delete");

        // Reload from file: still gone
        CsvItemRepository repo2 = new CsvItemRepository(file);
        assertFalse(repo2.findById("id-3").isPresent(),
                "Reloaded repo should not contain deleted item");
    }
}


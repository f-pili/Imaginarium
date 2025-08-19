package it.fpili.imaginarium.cli;

import it.fpili.imaginarium.Main;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Full end-to-end CLI integration test covering:
 * 1) Add/Update
 * 2) Delete
 * 3) List
 * 4) Search
 * 5) Show categories tree (Composite)
 * 6) Iterate items (custom Iterator)
 * 7) Export catalog to JSON
 * 0) Exit
 */
class CliIntegrationTest {

    private InputStream sysInBackup;
    private PrintStream sysOutBackup;
    private ByteArrayOutputStream out;

    @BeforeEach
    void setup() throws Exception {
        Path dataDir = Path.of("data");
        Files.createDirectories(dataDir);
        Files.deleteIfExists(dataDir.resolve("items.csv"));
        Files.deleteIfExists(dataDir.resolve("items.json"));

        sysInBackup = System.in;
        sysOutBackup = System.out;

        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void teardown() {
        System.setIn(sysInBackup);
        System.setOut(sysOutBackup);
    }

    @Test
    void fullCoverageFlow_allCommands() throws Exception {
        // Menu mapping (updated):
        // 1 Add/Update, 2 Delete, 3 List, 4 Search, 5 Tree, 6 Iterate, 7 Export, 0 Exit
        String userInput = String.join("\n",
                // Add two items
                "1", "id-100", "Sky Spoon", "Tools", "Scoops clouds",
                "1", "id-200", "Echo Jar", "Containers", "Stores echoes",
                // List (3)
                "3",
                // Search (4) for Spoon
                "4", "Spoon",
                // Show categories tree (5)
                "5",
                // Iterate items (6)
                "6",
                // Export to JSON (7)
                "7",
                // Delete one item (2) → delete id-200
                "2", "id-200",
                // List again (3) to confirm deletion
                "3",
                // Exit
                "0"
        ) + "\n";

        System.setIn(new ByteArrayInputStream(userInput.getBytes(StandardCharsets.UTF_8)));

        // Run the CLI
        Main.main(new String[0]);

        String output = out.toString(StandardCharsets.UTF_8);

        // Add/Update confirmations
        assertTrue(output.contains("Saved."), "Add/Update should confirm save");

        // First List output
        assertTrue(output.contains("Items:"), "List header should appear");
        assertTrue(output.contains("Sky Spoon"), "List should include Sky Spoon");
        assertTrue(output.contains("Echo Jar"), "List should include Echo Jar");

        // Search output
        assertTrue(output.contains("Matches:"), "Search header should appear");
        assertTrue(output.contains("Spoon"), "Search results should contain Spoon");

        // Composite tree output
        assertTrue(output.contains("Category: Tools"), "Composite should show 'Tools' category");
        assertTrue(output.contains("Category: Containers"), "Composite should show 'Containers' category");

        // Iterator output
        assertTrue(output.contains("Iterating items via custom Iterator:"), "Iterator header should appear");
        assertTrue(output.contains("Sky Spoon"), "Iterator should list Sky Spoon");
        assertTrue(output.contains("Echo Jar"), "Iterator should list Echo Jar");

        // JSON export file check
        Path jsonPath = Path.of("data", "items.json");
        assertTrue(Files.exists(jsonPath), "items.json should be created");
        String json = Files.readString(jsonPath, StandardCharsets.UTF_8);
        assertTrue(json.startsWith("{\"data\":["));
        assertTrue(json.contains("\"id\":\"id-100\""));
        assertTrue(json.contains("\"id\":\"id-200\""));
        assertTrue(json.endsWith("]}"));

        // Delete confirmation (may be implicit in log/stdout depending on Main)
        // At least ensure the second listing no longer shows the deleted item.
        // After delete, we listed again; verify Echo Jar is gone but Sky Spoon remains.
        // Grab the part of output after the delete to be strict (optional); or check globally:
        assertFalse(output.replaceFirst("(?s).*Deleted item with id=id-200", "")
                        .contains("Echo Jar"),
                "After deletion, 'Echo Jar' should not appear in the final list");
        assertTrue(output.contains("Sky Spoon"), "Sky Spoon should still be present");

        // Export confirmation in CLI
        assertTrue(output.contains("Exported to data/items.json"), "CLI should confirm JSON export");
    }
}




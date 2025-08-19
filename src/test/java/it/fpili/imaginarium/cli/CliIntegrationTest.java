package it.fpili.imaginarium.cli;

import it.fpili.imaginarium.Main;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Full end-to-end CLI integration test.
 *
 * <p>Covers all menu commands in a single user session:</p>
 * <ol>
 *   <li>Add/Update two items</li>
 *   <li>List items</li>
 *   <li>Search items</li>
 *   <li>Show categories tree (Composite)</li>
 *   <li>Iterate items (custom Iterator)</li>
 *   <li>Export catalog to JSON (Adapter)</li>
 *   <li>Delete one item and verify it disappears from listing</li>
 *   <li>Exit</li>
 * </ol>
 *
 * <p>Rationale: exercises the whole stack (sanitization → service → repository CSV →
 * exception shielding → CLI I/O) without mocking, ensuring that features integrate correctly.</p>
 */
class CliIntegrationTest {

    private InputStream sysInBackup;
    private PrintStream sysOutBackup;
    private ByteArrayOutputStream out;

    @BeforeEach
    void setup() throws Exception {
        // Arrange environment: ensure a clean data directory for a deterministic run
        Path dataDir = Path.of("data");
        Files.createDirectories(dataDir);
        Files.deleteIfExists(dataDir.resolve("items.csv"));
        Files.deleteIfExists(dataDir.resolve("items.json"));

        // Capture System.out and provide a fresh System.in later per test
        sysInBackup = System.in;
        sysOutBackup = System.out;
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void teardown() {
        // Restore original streams
        System.setIn(sysInBackup);
        System.setOut(sysOutBackup);
    }

    /**
     * Simulates a complete session hitting every CLI command in the current menu mapping:
     * 1 Add/Update, 2 Delete, 3 List, 4 Search, 5 Tree, 6 Iterate, 7 Export, 0 Exit.
     */
    @Test
    void fullCoverageFlow_allCommands() throws Exception {
        // Arrange input sequence for the whole session
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

        // Act: run the CLI main program
        Main.main(new String[0]);

        // Assert: verify the whole transcript and artifacts
        String output = out.toString(StandardCharsets.UTF_8);

        // Add/Update confirmations
        assertTrue(output.contains("Saved."), "Add/Update should confirm with 'Saved.' at least once");

        // First List output
        assertTrue(output.contains("Items:"), "List header 'Items:' should appear");
        assertTrue(output.contains("Sky Spoon"), "List should include 'Sky Spoon'");
        assertTrue(output.contains("Echo Jar"), "List should include 'Echo Jar' before deletion");

        // Search output
        assertTrue(output.contains("Matches:"), "Search header 'Matches:' should appear");
        assertTrue(output.contains("Spoon"), "Search results should contain 'Spoon'");

        // Composite tree output
        assertTrue(output.contains("Category: Tools"), "Composite should show category 'Tools'");
        assertTrue(output.contains("Category: Containers"), "Composite should show category 'Containers'");

        // Iterator output
        assertTrue(output.contains("Iterating items via custom Iterator:"),
                "Iterator header should appear");
        assertTrue(output.contains("Sky Spoon"), "Iterator should list 'Sky Spoon'");
        assertTrue(output.contains("Echo Jar"), "Iterator should list 'Echo Jar' before deletion");

        // JSON export file check
        Path jsonPath = Path.of("data", "items.json");
        assertTrue(Files.exists(jsonPath), "Export should create 'data/items.json'");
        String json = Files.readString(jsonPath, StandardCharsets.UTF_8);
        assertTrue(json.startsWith("{\"data\":["),
                "Exported JSON should start with {\"data\":[");
        assertTrue(json.contains("\"id\":\"id-100\""), "Exported JSON should contain id-100");
        assertTrue(json.contains("\"id\":\"id-200\""), "Exported JSON should contain id-200");
        assertTrue(json.endsWith("]}"), "Exported JSON should end with ]}");

        // After delete, verify 'Echo Jar' is gone from the final list, 'Sky Spoon' remains
        assertFalse(
                output.replaceFirst("(?s).*Deleted item with id=id-200", "")
                        .contains("Echo Jar"),
                "After deletion, 'Echo Jar' should not appear in the final list");
        assertTrue(output.contains("Sky Spoon"),
                "'Sky Spoon' should still be present after deleting 'Echo Jar'");

        // Export confirmation message
        assertTrue(output.contains("Exported to data/items.json"),
                "CLI should confirm JSON export to 'data/items.json'");
    }
}
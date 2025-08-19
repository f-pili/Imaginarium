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
 * 2) List
 * 3) Search
 * 4) Show categories tree (Composite)
 * 5) Iterate items (custom Iterator)
 * 6) Export catalog to JSON
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
        // Simulate a user session that hits every CLI feature
        String userInput = String.join("\n",
                // 1) Add/Update (item A)
                "1", "id-100", "Sky Spoon", "Tools", "Scoops clouds",
                // 1) Add/Update (item B)
                "1", "id-200", "Echo Jar", "Containers", "Stores echoes",
                // 2) List
                "2",
                // 3) Search (find Spoon)
                "3", "Spoon",
                // 4) Show categories tree (Composite)
                "4",
                // 5) Iterate items (custom Iterator)
                "5",
                // 6) Export catalog to JSON
                "6",
                // 0) Exit
                "0"
        ) + "\n";

        System.setIn(new ByteArrayInputStream(userInput.getBytes(StandardCharsets.UTF_8)));

        // Run CLI
        Main.main(new String[0]);

        String output = out.toString(StandardCharsets.UTF_8);

        // Add/Update confirmations
        assertTrue(output.contains("Saved."), "Add/Update should confirm save");

        // List output
        assertTrue(output.contains("Items:"), "List header should appear");
        assertTrue(output.contains("Sky Spoon"), "List should include item Sky Spoon");
        assertTrue(output.contains("Echo Jar"), "List should include item Echo Jar");

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

        // JSON export file
        Path jsonPath = Path.of("data", "items.json");
        assertTrue(Files.exists(jsonPath), "items.json should be created");
        String json = Files.readString(jsonPath, StandardCharsets.UTF_8);
        assertTrue(json.startsWith("{\"data\":["));
        assertTrue(json.contains("\"id\":\"id-100\""));
        assertTrue(json.contains("\"id\":\"id-200\""));
        assertTrue(json.endsWith("]}"));

        // Export confirmation in CLI
        assertTrue(output.contains("Exported to data/items.json"), "CLI should confirm JSON export");
    }
}



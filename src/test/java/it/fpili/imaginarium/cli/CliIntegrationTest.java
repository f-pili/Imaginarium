package it.fpili.imaginarium.cli;

import it.fpili.imaginarium.Main;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Full end-to-end CLI integration test for the Imaginarium application.
 *
 * <p>This test class covers all menu commands in a single user session:</p>
 * <ol>
 *   <li>Add/Update two items</li>
 *   <li>List items</li>
 *   <li>Search items</li>
 *   <li>Show categories tree (Composite pattern)</li>
 *   <li>Iterate items (custom Iterator pattern)</li>
 *   <li>Export catalog to JSON (Adapter pattern)</li>
 *   <li>Delete one item and verify it disappears from listing</li>
 *   <li>Exit</li>
 * </ol>
 *
 * <p>Rationale: exercises the whole stack (sanitization → service → repository CSV →
 * exception shielding → CLI I/O) without mocking, ensuring that features integrate correctly.</p>
 *
 */
class CliIntegrationTest {

    /** Backup of original System.in for restoration after test */
    private InputStream sysInBackup;

    /** Backup of original System.out for restoration after test */
    private PrintStream sysOutBackup;

    /** Output stream to capture System.out during test execution */
    private ByteArrayOutputStream out;

    /**
     * Sets up the test environment before each test execution.
     *
     * <p>This method:</p>
     * <ul>
     *   <li>Ensures a clean data directory for deterministic test runs</li>
     *   <li>Removes any existing CSV and JSON files to avoid interference</li>
     *   <li>Captures System.out for verification of CLI output</li>
     *   <li>Backs up original system streams for restoration</li>
     * </ul>
     *
     * @throws Exception if file system operations fail
     */
    @BeforeEach
    void setup() throws Exception {
        // Arrange environment: ensure a clean data directory for deterministic run
        Path dataDir = Path.of("data");
        Files.createDirectories(dataDir);
        Files.deleteIfExists(dataDir.resolve("items.csv"));
        Files.deleteIfExists(dataDir.resolve("items.json"));

        // Capture System.out and backup original streams
        sysInBackup = System.in;
        sysOutBackup = System.out;
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
    }

    /**
     * Restores the original system streams after each test execution.
     *
     * <p>This cleanup method ensures that subsequent tests or application runs
     * are not affected by the stream redirection performed during testing.</p>
     */
    @AfterEach
    void teardown() {
        // Restore original streams
        System.setIn(sysInBackup);
        System.setOut(sysOutBackup);
    }

    /**
     * Comprehensive end-to-end test that exercises all CLI commands.
     *
     * <p>This test simulates a complete user session by:</p>
     * <ol>
     *   <li><strong>Adding items:</strong> Creates two test items with different categories</li>
     *   <li><strong>Listing items:</strong> Verifies that items appear in the catalog</li>
     *   <li><strong>Searching:</strong> Tests search functionality with keyword matching</li>
     *   <li><strong>Category tree:</strong> Demonstrates Composite pattern with hierarchical display</li>
     *   <li><strong>Iterator pattern:</strong> Shows custom iterator implementation</li>
     *   <li><strong>JSON export:</strong> Tests Adapter pattern for format conversion</li>
     *   <li><strong>Item deletion:</strong> Verifies CRUD delete operation</li>
     *   <li><strong>Verification:</strong> Confirms deleted item no longer appears</li>
     * </ol>
     *
     * <p>The test verifies both the CLI output and the persistence layer by checking
     * that files are created correctly and contain expected data.</p>
     *
     * @throws Exception if any file I/O operations fail during test execution
     */
    @Test
    void fullCoverageFlow_allCommands() throws Exception {
        // Arrange: Prepare input sequence for the complete user session
        String userInput = String.join("\n",
                // Add first item: Sky Spoon in Tools category
                "1", "id-100", "Sky Spoon", "Tools", "Scoops clouds",
                // Add second item: Echo Jar in Containers category
                "1", "id-200", "Echo Jar", "Containers", "Stores echoes",
                // List all items to verify they were added
                "3",
                // Search for items containing "Spoon"
                "4", "Spoon",
                // Show categories tree (Composite pattern demonstration)
                "5",
                // Iterate items using custom iterator (Iterator pattern demonstration)
                "6",
                // Export catalog to JSON (Adapter pattern demonstration)
                "7",
                // Delete the Echo Jar item by ID
                "2", "id-200",
                // List items again to verify deletion
                "3",
                // Exit the application
                "0"
        ) + "\n";

        // Act: Execute the CLI application with simulated user input
        System.setIn(new ByteArrayInputStream(userInput.getBytes(StandardCharsets.UTF_8)));
        Main.main(new String[0]);

        // Assert: Verify the complete workflow execution and results
        String output = out.toString(StandardCharsets.UTF_8);

        // Verify item addition confirmations
        assertTrue(output.contains("Saved."),
                "Add/Update operations should confirm with 'Saved.' messages");

        // Verify listing functionality
        assertTrue(output.contains("Items:"),
                "List command should display 'Items:' header");
        assertTrue(output.contains("Sky Spoon"),
                "List should include 'Sky Spoon' item");
        assertTrue(output.contains("Echo Jar"),
                "List should include 'Echo Jar' before deletion");

        // Verify search functionality
        assertTrue(output.contains("Matches:"),
                "Search command should display 'Matches:' header");
        assertTrue(output.contains("Spoon"),
                "Search results should contain 'Spoon' keyword");

        // Verify Composite pattern (category tree)
        assertTrue(output.contains("Category: Tools"),
                "Composite pattern should display 'Tools' category");
        assertTrue(output.contains("Category: Containers"),
                "Composite pattern should display 'Containers' category");

        // Verify Iterator pattern
        assertTrue(output.contains("Iterating items via custom Iterator:"),
                "Iterator pattern should display iteration header");
        assertTrue(output.contains("Sky Spoon"),
                "Iterator should list 'Sky Spoon' item");
        assertTrue(output.contains("Echo Jar"),
                "Iterator should list 'Echo Jar' before deletion");

        // Verify JSON export (Adapter pattern)
        assertTrue(output.contains("Exported to data/items.json"),
                "JSON export should confirm successful export");

        // Verify JSON file creation and content
        Path jsonPath = Path.of("data", "items.json");
        assertTrue(Files.exists(jsonPath),
                "Export should create 'data/items.json' file");

        if (Files.exists(jsonPath)) {
            String json = Files.readString(jsonPath, StandardCharsets.UTF_8);
            assertTrue(json.startsWith("{\"data\":["),
                    "Exported JSON should start with proper structure");
            assertTrue(json.contains("\"id\":\"id-100\""),
                    "Exported JSON should contain first item");
            assertTrue(json.contains("\"id\":\"id-200\""),
                    "Exported JSON should contain second item");
            assertTrue(json.endsWith("]}"),
                    "Exported JSON should end with proper structure");
        }

        // Verify deletion functionality and output consistency
        String deletionMessage = "Successfully deleted item with ID: id-200";
        assertTrue(output.contains(deletionMessage),
                "Delete operation should confirm successful deletion");

        // Verify that deleted item no longer appears in subsequent output
        int deletionIndex = output.indexOf(deletionMessage);
        if (deletionIndex >= 0) {
            // Extract all output that appears after the deletion confirmation
            String outputAfterDeletion = output.substring(deletionIndex + deletionMessage.length());

            // Verify that the deleted item (Echo Jar) does not appear in any listing after deletion
            assertFalse(outputAfterDeletion.contains("Echo Jar"),
                    "Echo Jar should NOT appear in any output after deletion");

            // Verify that the remaining item (Sky Spoon) still appears
            assertTrue(outputAfterDeletion.contains("Sky Spoon"),
                    "Sky Spoon should still be present after deletion of Echo Jar");
        } else {
            fail("Could not find deletion confirmation message in output. " +
                    "This indicates the deletion operation may not have executed properly.");
        }
    }

    /**
     * Simplified test focusing specifically on the deletion workflow.
     *
     * <p>This test provides a minimal case for debugging deletion issues
     * without the complexity of the full integration test.</p>
     *
     * @throws Exception if any file I/O operations fail
     */
    @Test
    void testItemDeletionWorkflow() throws Exception {
        // Arrange: Minimal test case for deletion
        String userInput = String.join("\n",
                // Add a single test item
                "1", "test-id", "Test Item", "TestCategory", "Test Description",
                // Delete the same item
                "2", "test-id",
                // List items to verify deletion
                "3",
                // Exit
                "0"
        ) + "\n";

        // Act: Execute CLI with deletion workflow
        System.setIn(new ByteArrayInputStream(userInput.getBytes(StandardCharsets.UTF_8)));
        Main.main(new String[0]);

        // Assert: Verify deletion behavior
        String output = out.toString(StandardCharsets.UTF_8);

        assertTrue(output.contains("Successfully deleted item with ID: test-id"),
                "Should confirm deletion of test item");

        // Verify that deleted item doesn't appear after deletion confirmation
        String deletionMessage = "Successfully deleted item with ID: test-id";
        int deletionIndex = output.indexOf(deletionMessage);
        if (deletionIndex >= 0) {
            String afterDeletion = output.substring(deletionIndex + deletionMessage.length());
            assertFalse(afterDeletion.contains("Test Item"),
                    "Deleted item should not appear in any subsequent output");
        }
    }

    /**
     * Test focusing specifically on JSON export functionality.
     *
     * <p>This isolated test verifies that the Adapter pattern implementation
     * correctly converts CSV repository data to JSON format.</p>
     *
     * @throws Exception if any file I/O operations fail
     */
    @Test
    void testJsonExportFunctionality() throws Exception {
        // Arrange: Setup for JSON export test
        Path dataDir = Path.of("data");
        Files.createDirectories(dataDir);
        Files.deleteIfExists(dataDir.resolve("items.csv"));
        Files.deleteIfExists(dataDir.resolve("items.json"));

        String userInput = String.join("\n",
                // Add a test item
                "1", "export-test", "Export Test Item", "TestCategory", "Test export functionality",
                // Export to JSON
                "7",
                // Exit
                "0"
        ) + "\n";

        // Act: Execute CLI with focus on export
        System.setIn(new ByteArrayInputStream(userInput.getBytes(StandardCharsets.UTF_8)));
        Main.main(new String[0]);

        // Assert: Verify export functionality
        String output = out.toString(StandardCharsets.UTF_8);

        assertTrue(output.contains("Exported to data/items.json"),
                "Should confirm JSON export");

        Path jsonFile = Path.of("data", "items.json");
        assertTrue(Files.exists(jsonFile),
                "JSON file should be created");

        if (Files.exists(jsonFile)) {
            String jsonContent = Files.readString(jsonFile, StandardCharsets.UTF_8);
            assertTrue(jsonContent.contains("export-test"),
                    "JSON should contain the test item");
            assertTrue(jsonContent.contains("Export Test Item"),
                    "JSON should contain the item name");
        }
    }
}
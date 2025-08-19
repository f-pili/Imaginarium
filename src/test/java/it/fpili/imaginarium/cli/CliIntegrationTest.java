package it.fpili.imaginarium.cli;

import it.fpili.imaginarium.Main;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CliIntegrationTest {
    private InputStream sysInBackup;
    private PrintStream sysOutBackup;
    private ByteArrayOutputStream out;

    @BeforeEach
    void setup() throws Exception {
        // Ensure clean data folder for the CLI (relative path used by Main).
        Path dataDir = Path.of("data");
        Files.createDirectories(dataDir);
        Files.deleteIfExists(dataDir.resolve("items.csv"));

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
    void fullCliFlow_add_list_search_then_exit() throws Exception {
        String userInput = String.join("\n",
                "1",                 // Add/Update
                "id-100",
                "Sky Spoon",
                "Tools",
                "Scoops clouds",
                "2",                 // List
                "3",                 // Search
                "Spoon",
                "0"                  // Exit
        ) + "\n";

        System.setIn(new ByteArrayInputStream(userInput.getBytes(StandardCharsets.UTF_8)));

        // Run the CLI; it will read from the provided System.in and write to captured System.out.
        Main.main(new String[0]);

        String output = out.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Saved."), "Item should be saved");
        assertTrue(output.contains("Sky Spoon"), "Listing should show the item");
        assertTrue(output.contains("Matches:"), "Search should print matches header");
        assertTrue(output.contains("Spoon"), "Search results should contain the item");
    }
}

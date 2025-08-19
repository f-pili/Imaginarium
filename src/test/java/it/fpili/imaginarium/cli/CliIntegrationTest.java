package it.fpili.imaginarium.cli;

import it.fpili.imaginarium.Main;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

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
    void fullCliFlow_add_list_search_then_exit() {
        String userInput = String.join("\n",
                "1","id-100","Sky Spoon","Tools","Scoops clouds",
                "2","3","Spoon","0") + "\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes(StandardCharsets.UTF_8)));
        Main.main(new String[0]);
        String output = out.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Saved."));
        assertTrue(output.contains("Sky Spoon"));
        assertTrue(output.contains("Matches:"));
    }

    @Test
    void exportJsonEndToEnd_createsItemsJson() throws Exception {
        String userInput = String.join("\n",
                "1","id-200","Echo Jar","Containers","Stores echoes",
                "6", // Export catalog to JSON
                "0") + "\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes(StandardCharsets.UTF_8)));
        Main.main(new String[0]);

        Path jsonPath = Path.of("data","items.json");
        assertTrue(Files.exists(jsonPath), "items.json should be created");
        String json = Files.readString(jsonPath, StandardCharsets.UTF_8);
        assertTrue(json.startsWith("{\"data\":["));
        assertTrue(json.contains("\"id\":\"id-200\""));
        assertTrue(json.endsWith("]}"));

        String output = out.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Exported to data/items.json"));
    }
}


package it.fpili.imaginarium.factory;

import it.fpili.imaginarium.model.Item;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Factory Method implementation that builds {@link Item} objects.
 * <p>
 * Covers:
 * <ul>
 *   <li>Creation from sanitized user input ({@link UserInputItemCreator}).</li>
 *   <li>Creation from CSV columns ({@link CsvItemCreator}).</li>
 *   <li>Validation error when CSV columns are insufficient.</li>
 * </ul>
 * Rationale: ensures both concrete creators produce correct domain objects and
 * that basic preconditions are enforced.
 */
class ItemFactoryTest {

    @Test
    void userInputCreatorBuildsItem() {
        // Arrange
        UserInputItemCreator creator =
                new UserInputItemCreator("id-7", "Nimbus Brush", "Tools", "Paints on clouds");

        // Act
        Item it = creator.build();

        // Assert
        assertEquals("id-7", it.id(), "ID should match input");
        assertEquals("Nimbus Brush", it.name(), "Name should match input");
        assertEquals("Tools", it.category(), "Category should match input");
        assertEquals("Paints on clouds", it.description(), "Description should match input");
    }

    @Test
    void csvCreatorBuildsItem() {
        // Arrange
        String[] cols = {"id-8", "Whisper Box", "Containers", "Keeps secrets safe"};
        CsvItemCreator creator = new CsvItemCreator(cols);

        // Act
        Item it = creator.build();

        // Assert
        assertEquals("id-8", it.id(), "CSV column 0 should map to id");
        assertEquals("Whisper Box", it.name(), "CSV column 1 should map to name");
        assertEquals("Containers", it.category(), "CSV column 2 should map to category");
        assertEquals("Keeps secrets safe", it.description(), "CSV column 3 should map to description");
    }

    @Test
    void csvCreatorRejectsWhenColumnsAreLessThanFour() {
        // Arrange: only 3 columns provided (invalid)
        String[] bad = {"id-9", "Incomplete", "Cat"};

        // Act + Assert
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> new CsvItemCreator(bad),
                        "Constructor should reject fewer than 4 columns");
        assertTrue(ex.getMessage().toLowerCase().contains("expected 4"),
                "Error message should indicate the required number of columns");
    }
}

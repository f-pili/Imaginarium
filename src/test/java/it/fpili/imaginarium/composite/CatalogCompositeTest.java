package it.fpili.imaginarium.composite;

import it.fpili.imaginarium.model.Item;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for the Composite structure (CatalogCategory + CatalogItem).
 * <p>
 * Builds a small tree:
 * <pre>
 * Root Catalog
 *  ├─ Tools
 *  │   ├─ Sky Spoon
 *  │   └─ Nimbus Brush
 *  └─ Containers
 *      └─ Echo Jar
 * </pre>
 * and asserts that {@link CatalogComponent#showDetails()} prints a coherent view.
 * </p>
 */
class CatalogCompositeTest {

    @Test
    void demoComposite_printsTreeWithCategoriesAndItems() {
        // Arrange: build the tree
        CatalogCategory root = new CatalogCategory("Root Catalog");

        CatalogCategory tools = new CatalogCategory("Tools");
        tools.addComponent(new CatalogItem(
                new Item("id-10", "Sky Spoon", "Tools", "Scoops clouds")));
        tools.addComponent(new CatalogItem(
                new Item("id-11", "Nimbus Brush", "Tools", "Paints on clouds")));

        CatalogCategory containers = new CatalogCategory("Containers");
        containers.addComponent(new CatalogItem(
                new Item("id-20", "Echo Jar", "Containers", "Stores echoes")));

        root.addComponent(tools);
        root.addComponent(containers);

        // Capture System.out
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(bout));

        try {
            // Act
            root.showDetails();
        } finally {
            // Restore System.out
            System.setOut(original);
        }

        // Assert
        String out = bout.toString();
        assertTrue(out.contains("Category: Root Catalog"), "Root category should be printed");
        assertTrue(out.contains("Category: Tools"), "Subcategory 'Tools' should be printed");
        assertTrue(out.contains("Category: Containers"), "Subcategory 'Containers' should be printed");

        // Leaf lines include id, name, category, description
        assertTrue(out.contains("id-10") && out.contains("Sky Spoon") && out.contains("Tools") && out.contains("Scoops clouds"),
                "Leaf 'Sky Spoon' should be printed with all fields");
        assertTrue(out.contains("id-11") && out.contains("Nimbus Brush") && out.contains("Tools") && out.contains("Paints on clouds"),
                "Leaf 'Nimbus Brush' should be printed with all fields");
        assertTrue(out.contains("id-20") && out.contains("Echo Jar") && out.contains("Containers") && out.contains("Stores echoes"),
                "Leaf 'Echo Jar' should be printed with all fields");
    }
}


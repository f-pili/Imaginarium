package it.fpili.imaginarium.iterator;

import it.fpili.imaginarium.model.Item;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the custom {@link ItemIterator} implementation
 * used to traverse {@link CatalogItemCollection}.
 */
class ItemIteratorTest {

    @Test
    void iterateTwoItemsInSequence() {
        CatalogItemCollection col = new CatalogItemCollection();
        col.add(new Item("id-30","Star Lantern","Lights","Glows with starlight"));
        col.add(new Item("id-31","Dream Compass","Tools","Points to your dreams"));

        ItemIterator it = col.createIterator();

        assertTrue(it.hasNext(), "Iterator should start with elements");
        assertEquals("Star Lantern", it.next().name(), "First element should be Star Lantern");
        assertTrue(it.hasNext(), "Iterator should still have one element left");
        assertEquals("Dream Compass", it.next().name(), "Second element should be Dream Compass");
        assertFalse(it.hasNext(), "Iterator should be exhausted after consuming both items");
    }

    @Test
    void nextWithoutHasNextThrows() {
        CatalogItemCollection col = new CatalogItemCollection();
        ItemIterator it = col.createIterator();

        assertThrows(IllegalStateException.class, it::next,
                "Calling next() on empty iterator should throw");
    }
}


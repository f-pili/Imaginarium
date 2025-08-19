package it.fpili.imaginarium.iterator;

import it.fpili.imaginarium.model.Item;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemIteratorTest {

    @Test
    void iterateItems() {
        CatalogItemCollection col = new CatalogItemCollection();
        col.add(new Item("id-30","Star Lantern","Lights","Glows with starlight"));
        col.add(new Item("id-31","Dream Compass","Tools","Points to your dreams"));

        ItemIterator it = col.createIterator();
        assertTrue(it.hasNext());
        assertEquals("Star Lantern", it.next().name());
        assertTrue(it.hasNext());
        assertEquals("Dream Compass", it.next().name());
        assertFalse(it.hasNext());
    }
}

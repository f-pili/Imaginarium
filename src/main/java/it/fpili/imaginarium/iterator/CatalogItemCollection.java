package it.fpili.imaginarium.iterator;

import it.fpili.imaginarium.model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete collection of Items that can create an iterator.
 */
public final class CatalogItemCollection implements ItemCollection {
    private final List<Item> items = new ArrayList<>();

    public void add(Item item) {
        items.add(item);
    }

    @Override
    public ItemIterator createIterator() {
        return new CatalogItemIterator(this);
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public Item getAt(int index) {
        return items.get(index);
    }
}

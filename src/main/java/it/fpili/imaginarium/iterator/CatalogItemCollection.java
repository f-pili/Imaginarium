package it.fpili.imaginarium.iterator;

import it.fpili.imaginarium.model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete collection of {@link Item} objects.
 * <p>
 * Implements the {@link ItemCollection} interface and provides
 * the ability to create a custom iterator ({@link CatalogItemIterator}).
 * </p>
 */
public final class CatalogItemCollection implements ItemCollection {
    private final List<Item> items = new ArrayList<>();

    /**
     * Adds an {@link Item} to the collection.
     *
     * @param item the item to add
     */
    public void add(Item item) {
        items.add(item);
    }

    /**
     * Factory method that returns a new {@link CatalogItemIterator}
     * bound to this collection.
     *
     * @return iterator over the collection
     */
    @Override
    public ItemIterator createIterator() {
        return new CatalogItemIterator(this);
    }

    /**
     * @return number of items in the collection
     */
    @Override
    public int size() {
        return items.size();
    }

    /**
     * Retrieves the item at the given index.
     *
     * @param index position in the collection
     * @return the {@link Item} at the given index
     */
    @Override
    public Item getAt(int index) {
        return items.get(index);
    }
}


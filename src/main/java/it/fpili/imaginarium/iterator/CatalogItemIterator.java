package it.fpili.imaginarium.iterator;

import it.fpili.imaginarium.model.Item;

/**
 * Concrete Iterator for {@link CatalogItemCollection}.
 * <p>
 * Encapsulates iteration logic over a collection of {@link Item} objects
 * without exposing its internal representation.
 * </p>
 */
public final class CatalogItemIterator implements ItemIterator {
    private final CatalogItemCollection collection;
    private int position = 0;

    /**
     * Creates an iterator bound to a given collection.
     *
     * @param collection the {@link CatalogItemCollection} to iterate over
     */
    public CatalogItemIterator(CatalogItemCollection collection) {
        this.collection = collection;
    }

    /**
     * Checks if there are more elements to visit.
     *
     * @return {@code true} if another element exists, {@code false} otherwise
     */
    @Override
    public boolean hasNext() {
        return position < collection.size();
    }

    /**
     * Returns the next element in the collection.
     *
     * @return the next {@link Item}
     * @throws IllegalStateException if no more elements are available
     */
    @Override
    public Item next() {
        if (!hasNext()) {
            throw new IllegalStateException("No more elements");
        }
        return collection.getAt(position++);
    }
}


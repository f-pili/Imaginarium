package it.fpili.imaginarium.iterator;

import it.fpili.imaginarium.model.Item;

/**
 * Concrete Iterator for CatalogItemCollection.
 */
public final class CatalogItemIterator implements ItemIterator {
    private final CatalogItemCollection collection;
    private int position = 0;

    public CatalogItemIterator(CatalogItemCollection collection) {
        this.collection = collection;
    }

    @Override
    public boolean hasNext() {
        return position < collection.size();
    }

    @Override
    public Item next() {
        if (!hasNext()) {
            throw new IllegalStateException("No more elements");
        }
        return collection.getAt(position++);
    }
}

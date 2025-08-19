package it.fpili.imaginarium.iterator;

import it.fpili.imaginarium.model.Item;

/**
 * Aggregate interface defining creation of an ItemIterator.
 */
public interface ItemCollection {
    ItemIterator createIterator();
    int size();
    Item getAt(int index);
}

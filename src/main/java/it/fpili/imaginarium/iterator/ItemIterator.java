package it.fpili.imaginarium.iterator;

import it.fpili.imaginarium.model.Item;

/**
 * Iterator interface for sequential traversal of Items.
 */
public interface ItemIterator {
    boolean hasNext();
    Item next();
}

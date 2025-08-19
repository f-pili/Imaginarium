package it.fpili.imaginarium.iterator;

import it.fpili.imaginarium.model.Item;

/**
 * Aggregate interface in the Iterator pattern.
 * <p>
 * Defines the contract for a collection of {@link Item} objects
 * that can produce an {@link ItemIterator} to traverse its elements
 * without exposing the underlying representation.
 * </p>
 */
public interface ItemCollection {

    /**
     * Factory method to create a new iterator for this collection.
     *
     * @return a fresh {@link ItemIterator} bound to this collection
     */
    ItemIterator createIterator();

    /**
     * Returns the number of elements contained in the collection.
     *
     * @return the collection size
     */
    int size();

    /**
     * Provides direct access to the element at the given index.
     * <p>
     * Normally used internally by iterators; external clients should
     * prefer using the iterator abstraction instead.
     * </p>
     *
     * @param index zero-based index
     * @return the {@link Item} at the given index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    Item getAt(int index);
}
package it.fpili.imaginarium.iterator;

import it.fpili.imaginarium.model.Item;

/**
 * Iterator interface for sequential traversal of {@link Item} elements.
 * <p>
 * Defines the operations for checking if more elements are available
 * and retrieving the next element in the sequence. This abstraction
 * allows clients to traverse collections without knowing their internal
 * structure.
 * </p>
 */
public interface ItemIterator {

    /**
     * Checks if the iteration has more elements.
     *
     * @return {@code true} if another {@link Item} is available,
     *         {@code false} otherwise
     */
    boolean hasNext();

    /**
     * Returns the next element in the iteration.
     *
     * @return the next {@link Item}
     * @throws IllegalStateException if no more elements are available
     */
    Item next();
}
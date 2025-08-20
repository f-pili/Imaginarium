package it.fpili.imaginarium.factory;

import it.fpili.imaginarium.model.Item;

/**
 * Abstract Creator role in the Factory Method pattern.
 * Defines {@link #createItem()} and provides a template operation {@link #build()}.
 */
public abstract class ItemCreator {

    /**
     * Factory Method to be implemented by concrete creators.
     * @return a concrete {@link Item} instance
     */
    protected abstract Item createItem();

    /**
     * Template Method that delegates instantiation to {@link #createItem()}.
     * Common pre/post logic can be added here if needed.
     * @return the created {@link Item}
     */
    public Item build() {
        return createItem(); // no redundant local variable
    }
}
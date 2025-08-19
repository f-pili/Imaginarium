package it.fpili.imaginarium.factory;

import it.fpili.imaginarium.model.Item;

/**
 * Abstract Creator in the Factory Method pattern.
 * Subclasses decide which concrete Item to instantiate.
 * The template method {@link #build()} can include common steps.
 */
public abstract class ItemCreator {

    /**
     * Factory Method – implemented by concrete creators.
     * @return a concrete Item instance
     */
    protected abstract Item createItem();

    /**
     * Template operation that uses the factory method.
     * Here we could add shared steps (logging, defaults, post-checks).
     * @return the created Item
     */
    public Item build() {
        Item item = createItem();
        // Hook for future validations or defaults.
        return item;
    }
}

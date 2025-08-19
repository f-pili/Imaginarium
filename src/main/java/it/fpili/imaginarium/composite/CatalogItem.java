package it.fpili.imaginarium.composite;

import it.fpili.imaginarium.model.Item;

/**
 * Leaf node representing a single Item in the catalog.
 */
public final class CatalogItem implements CatalogComponent {
    private final Item item;

    public CatalogItem(Item item) {
        this.item = item;
    }

    @Override
    public void showDetails() {
        System.out.println("- " + item.id() + " | " + item.name() + " | " +
                item.category() + " | " + item.description());
    }
}

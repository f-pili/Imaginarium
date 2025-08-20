package it.fpili.imaginarium.composite;

import it.fpili.imaginarium.model.Item;
import java.util.Objects;

/**
 * Leaf node representing a single {@link Item} in the catalog.
 * <p>
 * This is the terminal element in the Composite structure:
 * it cannot contain children, only data from a domain {@code Item}.
 * </p>
 */
public final class CatalogItem implements CatalogComponent {

    /** Domain object backing this leaf node (immutable reference). */
    private final Item item;

    /**
     * Creates a catalog leaf from a domain {@link Item}.
     *
     * @param item the item to wrap; must not be {@code null}
     * @throws NullPointerException if {@code item} is {@code null}
     */
    public CatalogItem(Item item) {
        this.item = Objects.requireNonNull(item, "item");
    }

    /**
     * Prints details of the wrapped {@link Item}.
     * This is the leaf's implementation of the Composite operation.
     */
    @Override
    public void showDetails() {
        System.out.println("- " + item.id() + " | "
                + item.name() + " | "
                + item.category() + " | "
                + item.description());
    }
}
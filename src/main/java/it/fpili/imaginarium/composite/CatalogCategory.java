package it.fpili.imaginarium.composite;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Composite node representing a catalog category.
 * <p>
 * A {@code CatalogCategory} can contain both leaf nodes ({@link CatalogItem})
 * and other {@link CatalogCategory} instances, enabling a tree of arbitrary depth.
 * </p>
 * <p><strong>Note:</strong> this class is not thread-safe; all access is expected
 * to happen from the single-threaded CLI.</p>
 */
public final class CatalogCategory implements CatalogComponent {

    /** Human-friendly category name (immutable). */
    private final String name;

    /** Children components (subcategories or items). */
    private final List<CatalogComponent> children = new ArrayList<>();

    /**
     * Creates a category node.
     *
     * @param name category name; must not be {@code null}
     * @throws NullPointerException if {@code name} is {@code null}
     */
    public CatalogCategory(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    /**
     * Adds a child component to this category.
     *
     * @param c component to add; must not be {@code null}
     * @throws NullPointerException if {@code c} is {@code null}
     */
    public void addComponent(CatalogComponent c) {
        children.add(Objects.requireNonNull(c, "component"));
    }

    /**
     * Removes a child component from this category (no-op if not present).
     *
     * @param c component to remove; must not be {@code null}
     * @throws NullPointerException if {@code c} is {@code null}
     */
    public void removeComponent(CatalogComponent c) {
        children.remove(Objects.requireNonNull(c, "component"));
    }

    /**
     * Prints this category and recursively prints its children.
     * This is the Composite's uniform operation used by the client.
     */
    @Override
    public void showDetails() {
        System.out.println("Category: " + name);
        for (CatalogComponent c : children) {
            c.showDetails();
        }
    }
}


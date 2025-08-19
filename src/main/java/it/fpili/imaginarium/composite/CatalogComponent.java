package it.fpili.imaginarium.composite;

/**
 * Common abstraction for nodes in the catalog tree
 * (Composite design pattern).
 * <p>
 * Both composite nodes ({@link CatalogCategory}) and
 * leaf nodes ({@link CatalogItem}) implement this interface
 * so they can be treated uniformly.
 * </p>
 */
public interface CatalogComponent {

    /**
     * Displays details of this node to standard output.
     * <p>
     * For a composite node, this includes recursively
     * invoking {@code showDetails()} on its children.
     * For a leaf node, this prints the item attributes.
     * </p>
     */
    void showDetails();
}

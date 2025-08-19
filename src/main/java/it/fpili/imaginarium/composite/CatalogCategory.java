package it.fpili.imaginarium.composite;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite node representing a category that can contain subcategories or items.
 */
public final class CatalogCategory implements CatalogComponent {
    private final String name;
    private final List<CatalogComponent> children = new ArrayList<>();

    public CatalogCategory(String name) {
        this.name = name;
    }

    public void addComponent(CatalogComponent c) {
        children.add(c);
    }

    public void removeComponent(CatalogComponent c) {
        children.remove(c);
    }

    @Override
    public void showDetails() {
        System.out.println("Category: " + name);
        for (CatalogComponent c : children) {
            c.showDetails();
        }
    }
}

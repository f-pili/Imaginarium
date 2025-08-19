package it.fpili.imaginarium.factory;

import it.fpili.imaginarium.model.Item;
import java.util.Objects;

/**
 * Concrete Creator for items coming from sanitized user input.
 */
public final class UserInputItemCreator extends ItemCreator {
    private final String id;
    private final String name;
    private final String category;
    private final String description;

    public UserInputItemCreator(String id, String name, String category, String description) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNullElse(name, "");
        this.category = Objects.requireNonNullElse(category, "");
        this.description = Objects.requireNonNullElse(description, "");
    }

    @Override
    protected Item createItem() {
        return new Item(id, name, category, description);
    }
}

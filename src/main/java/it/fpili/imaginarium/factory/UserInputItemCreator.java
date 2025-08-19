package it.fpili.imaginarium.factory;

import it.fpili.imaginarium.model.Item;
import java.util.Objects;

/**
 * Concrete Creator in the Factory Method pattern.
 * <p>
 * Specialization of {@link ItemCreator} that builds {@link Item} instances
 * starting from sanitized user-provided input.
 * </p>
 */
public final class UserInputItemCreator extends ItemCreator {
    private final String id;
    private final String name;
    private final String category;
    private final String description;

    /**
     * Initializes the creator with user input values.
     * Null values for optional fields are replaced with an empty string.
     *
     * @param id          unique identifier (not null)
     * @param name        item name (nullable → empty)
     * @param category    item category (nullable → empty)
     * @param description item description (nullable → empty)
     */
    public UserInputItemCreator(String id, String name, String category, String description) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.name = Objects.requireNonNullElse(name, "");
        this.category = Objects.requireNonNullElse(category, "");
        this.description = Objects.requireNonNullElse(description, "");
    }

    /**
     * Factory Method implementation returning a new {@link Item}.
     *
     * @return created item
     */
    @Override
    protected Item createItem() {
        return new Item(id, name, category, description);
    }
}


package it.fpili.imaginarium.model;

import java.util.Objects;

/**
 * Value object representing an item in the Imaginarium catalog.
 * <p>
 * Instances are immutable: all fields are set in the constructor
 * and cannot be changed afterward. Equality and hash code are
 * based solely on the {@code id}, which is the unique identifier.
 * </p>
 */
public final class Item {
    private final String id;
    private final String name;
    private final String category;
    private final String description;

    /**
     * Constructs a new immutable {@link Item}.
     *
     * @param id          unique identifier (non-null, non-empty)
     * @param name        human-friendly display name (nullable, defaults to empty)
     * @param category    logical category label (nullable, defaults to empty)
     * @param description short description (nullable, defaults to empty)
     * @throws IllegalArgumentException if {@code id} is empty after trimming
     * @throws NullPointerException     if {@code id} is {@code null}
     */
    public Item(String id, String name, String category, String description) {
        this.id = Objects.requireNonNull(id, "id").trim();
        this.name = Objects.requireNonNullElse(name, "").trim();
        this.category = Objects.requireNonNullElse(category, "").trim();
        this.description = Objects.requireNonNullElse(description, "").trim();
        if (this.id.isEmpty()) throw new IllegalArgumentException("id cannot be empty");
    }

    /** @return the unique identifier of this item */
    public String id() { return id; }

    /** @return the display name of this item (never null) */
    public String name() { return name; }

    /** @return the category label of this item (never null) */
    public String category() { return category; }

    /** @return the description of this item (never null) */
    public String description() { return description; }

    /**
     * Equality is based only on {@code id}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item i)) return false;
        return id.equals(i.id);
    }

    /**
     * Hash code is derived from {@code id}.
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Item[" + id + "," + name + "," + category + "]";
    }
}

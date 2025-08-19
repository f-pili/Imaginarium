package it.fpili.imaginarium.model;

import java.util.Objects;

/**
 * Value object representing an imaginary catalog item.
 */
public final class Item {
    private final String id;
    private final String name;
    private final String category;
    private final String description;

    /**
     * Creates an immutable Item.
     * @param id unique identifier (non-null, non-empty)
     * @param name human-friendly name
     * @param category logical category label
     * @param description short description
     */
    public Item(String id, String name, String category, String description) {
        this.id = Objects.requireNonNull(id, "id").trim();
        this.name = Objects.requireNonNullElse(name, "").trim();
        this.category = Objects.requireNonNullElse(category, "").trim();
        this.description = Objects.requireNonNullElse(description, "").trim();
        if (this.id.isEmpty()) throw new IllegalArgumentException("id cannot be empty");
    }

    public String id() { return id; }
    public String name() { return name; }
    public String category() { return category; }
    public String description() { return description; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item i)) return false;
        return id.equals(i.id);
    }
    @Override public int hashCode() { return id.hashCode(); }
    @Override public String toString() { return "Item[" + id + "," + name + "," + category + "]"; }
}

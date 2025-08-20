package it.fpili.imaginarium.factory;

import it.fpili.imaginarium.model.Item;
import java.util.Objects;

/**
 * Concrete Creator in the Factory Method pattern.
 * <p>
 * Responsible for creating {@link Item} instances from a CSV line,
 * expected to contain exactly 4 columns:
 * <ul>
 *   <li>ID</li>
 *   <li>Name</li>
 *   <li>Category</li>
 *   <li>Description</li>
 * </ul>
 * </p>
 */
public final class CsvItemCreator extends ItemCreator {
    private final String[] cols;

    /**
     * Constructs a CsvItemCreator with the provided CSV columns.
     *
     * @param cols a non-null array of 4 columns
     * @throws IllegalArgumentException if fewer than 4 columns are provided
     * @throws NullPointerException     if cols is null
     */
    public CsvItemCreator(String[] cols) {
        this.cols = Objects.requireNonNull(cols, "cols");
        if (cols.length < 4) {
            throw new IllegalArgumentException("Expected 4 columns");
        }
    }

    /**
     * Creates an {@link Item} from the 4 CSV columns.
     *
     * @return a new {@link Item} populated with id, name, category, and description
     */
    @Override
    protected Item createItem() {
        return new Item(cols[0], cols[1], cols[2], cols[3]);
    }
}
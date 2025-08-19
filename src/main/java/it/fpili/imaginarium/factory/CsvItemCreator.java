package it.fpili.imaginarium.factory;

import it.fpili.imaginarium.model.Item;
import java.util.Objects;

/**
 * Concrete Creator for items parsed from a CSV line (4 columns).
 */
public final class CsvItemCreator extends ItemCreator {
    private final String[] cols;

    public CsvItemCreator(String[] cols) {
        this.cols = Objects.requireNonNull(cols, "cols");
        if (cols.length < 4) throw new IllegalArgumentException("Expected 4 columns");
    }

    @Override
    protected Item createItem() {
        return new Item(cols[0], cols[1], cols[2], cols[3]);
    }
}

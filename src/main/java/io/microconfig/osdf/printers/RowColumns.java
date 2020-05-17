package io.microconfig.osdf.printers;

import java.util.List;

public interface RowColumns {
    default void add(RowColumns other) {
        synchronized (this) {
            if (other.getColumns().equals(getColumns())) {
                getRows().addAll(other.getRows());
            }
        }
    }

    List<String> getColumns();

    List<List<String>> getRows();
}

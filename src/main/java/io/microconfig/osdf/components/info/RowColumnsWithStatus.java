package io.microconfig.osdf.components.info;

import io.microconfig.osdf.printers.RowColumns;

public interface RowColumnsWithStatus extends RowColumns {
    boolean getStatus();
}

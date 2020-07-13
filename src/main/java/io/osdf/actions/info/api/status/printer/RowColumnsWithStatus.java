package io.osdf.actions.info.api.status.printer;

import io.osdf.actions.info.printer.RowColumns;

public interface RowColumnsWithStatus extends RowColumns {
    boolean getStatus();
}

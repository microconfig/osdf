package io.osdf.actions.info.api.status;

import io.osdf.actions.info.api.status.printer.RowColumnsWithStatus;
import io.osdf.actions.info.printer.ColumnPrinter;
import io.osdf.core.application.core.Application;

public interface AppStatusRowsGetter {
    RowColumnsWithStatus statusOf(Application app, ColumnPrinter printer);
}

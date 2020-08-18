package io.osdf.actions.info.api.status.printer;

import io.osdf.actions.info.printer.RowColumns;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RowColumnsWithStatusImpl implements RowColumnsWithStatus {
    private final RowColumns rowColumns;
    private final boolean status;

    public static RowColumnsWithStatusImpl rowColumnsWithStatus(RowColumns rowColumns, boolean status) {
        return new RowColumnsWithStatusImpl(rowColumns, status);
    }

    @Override
    public boolean getStatus() {
        return status;
    }

    @Override
    public List<String> getColumns() {
        return rowColumns.getColumns();
    }

    @Override
    public List<List<String>> getRows() {
        return rowColumns.getRows();
    }
}

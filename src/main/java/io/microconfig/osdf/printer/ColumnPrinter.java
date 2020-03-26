package io.microconfig.osdf.printer;

import io.microconfig.utils.Logger;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static io.microconfig.osdf.utils.StringUtils.pad;
import static io.microconfig.utils.Logger.announce;
import static java.lang.Math.max;
import static java.util.List.of;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

@RequiredArgsConstructor
public class ColumnPrinter {
    private final List<String> columns;
    private final List<List<String>> rows;
    private final List<Consumer<String>> printers;

    public static ColumnPrinter printer() {
        return new ColumnPrinter(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public void addColumns(String... columns) {
        this.columns.addAll(of(columns));
    }

    public void addRow(String... row) {
        addRow(Logger::announce, row);
    }

    public void addRow(Consumer<String> printer, String... row) {
        rows.add(of(row));
        printers.add(printer);
    }

    public void print() {
        List<Integer> pads = pads();
        announce(formattedRow(columns, pads));
        for (int i = 0; i < rows.size(); i++) {
            printers.get(i).accept(formattedRow(rows.get(i), pads));
        }
    }

    public String formattedRow(List<String> row, List<Integer> pads) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < row.size(); i++) {
            result.append(pad(row.get(i), pads.get(i)));
        }
        return result.toString();
    }

    private List<Integer> pads() {
        List<Integer> pads = columns.stream().map(String::length).collect(toList());
        for (List<String> row : rows) {
            range(0, row.size())
                    .forEach(i -> pads.set(i, max(pads.get(i), row.get(i).length())));
        }
        return pads.stream().map(pad -> pad + 4).collect(toList());
    }
}

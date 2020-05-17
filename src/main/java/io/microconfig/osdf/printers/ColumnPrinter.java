package io.microconfig.osdf.printers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

import static io.microconfig.osdf.utils.StringUtils.*;
import static io.microconfig.utils.Logger.announce;
import static io.microconfig.utils.Logger.info;
import static java.lang.Math.max;
import static java.util.List.of;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.IntStream.range;

@RequiredArgsConstructor
@Getter
public class ColumnPrinter implements RowColumns {
    private final List<String> columns;
    private final List<List<String>> rows;

    public static ColumnPrinter printer() {
        return new ColumnPrinter(new ArrayList<>(), new ArrayList<>());
    }

    public void addColumns(String... columns) {
        this.columns.addAll(of(columns));
    }

    public synchronized void addRow(String... row) {
        rows.add(of(row));
    }

    public void print() {
        List<Integer> pads = pads();
        announce(formattedRow(columns, pads));
        range(0, rows.size())
                .forEach(i -> info(formattedRow(rows.get(i), pads)));
    }

    public ColumnPrinter newPrinter() {
        ColumnPrinter printer = printer();
        printer.addColumns(columns.toArray(new String[0]));
        return printer;
    }

    private String formattedRow(List<String> row, List<Integer> pads) {
        return range(0, row.size())
                .mapToObj(i -> coloredStringPad(row.get(i), pads.get(i)))
                .collect(joining());
    }

    private List<Integer> pads() {
        List<Integer> pads = columns.stream()
                .map(String::length)
                .collect(Collectors.toList());
        rows.forEach(row -> range(0, row.size()).forEach(setMaxPad(pads, row)));
        return pads.stream().map(pad -> pad + 4).collect(toUnmodifiableList());
    }

    private IntConsumer setMaxPad(List<Integer> pads, List<String> row) {
        return i -> pads.set(i, max(pads.get(i), coloredStringLength(row.get(i))));
    }
}

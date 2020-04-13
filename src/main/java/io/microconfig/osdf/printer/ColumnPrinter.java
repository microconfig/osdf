package io.microconfig.osdf.printer;

import io.microconfig.utils.Logger;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

import static io.microconfig.osdf.utils.StringUtils.pad;
import static io.microconfig.utils.Logger.announce;
import static java.lang.Math.max;
import static java.util.List.of;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableList;
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

    public synchronized void addRow(String... row) {
        addRow(Logger::announce, row);
    }

    public synchronized void addRow(Consumer<String> printer, String... row) {
        rows.add(of(row));
        printers.add(printer);
    }

    public synchronized void addRows(ColumnPrinter other) {
        rows.addAll(other.rows);
        printers.addAll(other.printers);
    }

    public void print() {
        List<Integer> pads = pads();
        announce(formattedRow(columns, pads));
        range(0, rows.size())
                .forEach(i -> printers.get(i)
                .accept(formattedRow(rows.get(i), pads)));
    }

    public ColumnPrinter newPrinter() {
        ColumnPrinter printer = printer();
        printer.addColumns(columns.toArray(new String[0]));
        return printer;
    }

    public String formattedRow(List<String> row, List<Integer> pads) {
        return range(0, row.size())
                .mapToObj(i -> pad(row.get(i), pads.get(i)))
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
        return i -> pads.set(i, max(pads.get(i), row.get(i).length()));
    }
}

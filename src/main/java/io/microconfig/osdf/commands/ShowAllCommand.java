package io.microconfig.osdf.commands;

import io.cluster.old.cluster.cli.ClusterCLI;
import io.microconfig.osdf.printers.ColumnPrinter;
import lombok.RequiredArgsConstructor;

import static java.util.stream.Stream.of;

@RequiredArgsConstructor
public class ShowAllCommand {
    private final ClusterCLI cli;
    private final ColumnPrinter printer;

    public void run() {
        of("job", "deployment", "dc")
                .map(line -> cli.execute("get " + line + " -o custom-columns=" +
                        "name:.metadata.labels.application," +
                        "configVersion:.metadata.labels.projectVersion")
                        .getOutputLines())
                .forEach(lines -> lines.stream()
                        .filter(line -> !line.contains("<none>") && !line.contains("name"))
                        .map(line -> line.split("\\s+"))
                        .forEach(printer::addRow));
        printer.addColumns("NAME", "VERSION");
        printer.print();
    }
}
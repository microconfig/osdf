package io.osdf.actions.info.api.showall;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.actions.info.printer.ColumnPrinter;
import lombok.RequiredArgsConstructor;

import static java.util.stream.Stream.of;

@RequiredArgsConstructor
public class ShowAllCommand {
    private final ClusterCli cli;
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
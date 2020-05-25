package io.microconfig.osdf.commands;

import io.microconfig.osdf.openshift.OpenShiftCLI;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.printers.ColumnPrinter;
import lombok.RequiredArgsConstructor;

import static java.util.stream.Stream.*;

@RequiredArgsConstructor
public class ShowAllCommand {
    private final OpenShiftCLI oc;
    private final ColumnPrinter printer;

    public void run() {
        of("job", "deployment", "dc")
                .map(line -> oc.execute("oc get " + line + " -o custom-columns=" +
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
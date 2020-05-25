package io.microconfig.osdf.commands;

import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.OpenShiftProject;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.printers.ColumnPrinter;
import lombok.RequiredArgsConstructor;
import java.util.stream.Stream;

import static io.microconfig.osdf.openshift.OpenShiftProject.create;

@RequiredArgsConstructor
public class ShowAllCommand {
    private final OSDFPaths paths;
    private final OCExecutor oc;
    private final ColumnPrinter printer;

    public void run() {
        try (OpenShiftProject ignored = create(paths, oc).connect()) {
            Stream.of("job", "deployment", "dc")
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
}

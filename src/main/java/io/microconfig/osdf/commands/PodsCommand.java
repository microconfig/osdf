package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.components.checker.HealthChecker;
import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.OpenShiftProject;
import io.microconfig.osdf.printers.ColumnPrinter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.components.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.openshift.OpenShiftProject.create;

@RequiredArgsConstructor
public class PodsCommand {
    private final OSDFPaths paths;
    private final OCExecutor oc;
    private final HealthChecker healthChecker;
    private final ColumnPrinter printer;

    public void show(List<String> components) {
        try (OpenShiftProject ignored = create(paths, oc).connect()) {
            printer.addColumns("COMPONENT", "POD", "HEALTH");

            componentsLoader(paths.componentsPath(), components, oc)
                    .load(DeploymentComponent.class)
                    .forEach(component -> getPodsInfo(healthChecker, printer, component));

            printer.print();
        }
    }

    private void getPodsInfo(HealthChecker healthChecker, ColumnPrinter printer, DeploymentComponent component) {
        component.pods().forEach(pod -> printer.addRow(component.getName(), pod.getName(), healthChecker.check(pod) ? "OK" : "BAD"));
    }
}

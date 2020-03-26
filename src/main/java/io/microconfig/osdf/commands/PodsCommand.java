package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.components.checker.HealthChecker;
import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.OpenShiftProject;
import io.microconfig.osdf.openshift.Pod;
import io.microconfig.osdf.printer.ColumnPrinter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.components.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.openshift.OpenShiftProject.create;
import static io.microconfig.osdf.printer.ColumnPrinter.printer;

@RequiredArgsConstructor
public class PodsCommand {
    private final OSDFPaths paths;
    private final OCExecutor oc;
    private final HealthChecker healthChecker;

    public void show(List<String> components) {
        try (OpenShiftProject ignored = create(paths, oc).connect()) {
            ColumnPrinter printer = printer("COMPONENT", "POD", "HEALTH");

            componentsLoader(paths.componentsPath(), components, oc)
                    .load(DeploymentComponent.class)
                    .forEach(component -> getPodsInfo(healthChecker, printer, component));

            printer.print();
        }
    }

    private void getPodsInfo(HealthChecker healthChecker, ColumnPrinter printer, DeploymentComponent component) {
        List<Pod> pods = component.pods();
        for (Pod pod : pods) {
            boolean status = healthChecker.check(pod);
            printer.addRow(component.getName(), pod.getName(), status ? "OK" : "BAD");
        }
    }
}

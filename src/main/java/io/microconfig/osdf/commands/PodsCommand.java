package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.components.checker.LogHealthChecker;
import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.microconfig.properties.HealthCheckProperties;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.OpenShiftProject;
import io.microconfig.osdf.openshift.Pod;
import io.microconfig.osdf.printer.ColumnPrinter;
import io.microconfig.osdf.state.OSDFState;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.components.checker.LogHealthChecker.logHealthChecker;
import static io.microconfig.osdf.components.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.microconfig.properties.HealthCheckProperties.properties;
import static io.microconfig.osdf.microconfig.properties.PropertyGetter.propertyGetter;
import static io.microconfig.osdf.openshift.OpenShiftProject.create;
import static io.microconfig.osdf.printer.ColumnPrinter.printer;

@RequiredArgsConstructor
public class PodsCommand {
    private final OSDFPaths paths;
    private final OCExecutor oc;


    public void show(List<String> components) {
        try (OpenShiftProject ignored = create(paths, oc).connect()) {
            ColumnPrinter printer = printer("COMPONENT", "POD", "HEALTH");
            String env = OSDFState.fromFile(paths.stateSavePath()).getEnv();
            HealthCheckProperties properties = properties(propertyGetter(env, paths.configPath()));

            componentsLoader(paths.componentsPath(), components, oc)
                    .load(DeploymentComponent.class)
                    .forEach(component -> getPodsInfo(properties, printer, component));

            printer.print();
        }
    }

    private void getPodsInfo(HealthCheckProperties properties, ColumnPrinter printer, DeploymentComponent component) {
        List<Pod> pods = component.pods();
        LogHealthChecker checker = logHealthChecker(component, properties);
        for (Pod pod : pods) {
            boolean status = checker.checkPod(pod);
            printer.addRow(component.getName(), pod.getName(), status ? "OK" : "BAD");
        }
    }
}

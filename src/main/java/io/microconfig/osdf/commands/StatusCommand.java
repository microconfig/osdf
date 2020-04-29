package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.components.JobComponent;
import io.microconfig.osdf.components.checker.HealthChecker;
import io.microconfig.osdf.components.loader.ComponentsLoaderImpl;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.exceptions.StatusCodeException;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.OpenShiftProject;
import io.microconfig.osdf.printers.ColumnPrinter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.components.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.openshift.OpenShiftProject.create;
import static io.microconfig.osdf.printers.StatusPrinter.statusPrinter;

@RequiredArgsConstructor
public class StatusCommand {
    private final OSDFPaths paths;
    private final OCExecutor oc;
    private final HealthChecker healthChecker;
    private final ColumnPrinter printer;

    public void run(List<String> components) {
        try (OpenShiftProject ignored = create(paths, oc).connect()) {
            if (!checkStatusAndPrint(components)) {
                throw new StatusCodeException(1);
            }
        }
    }

    private boolean checkStatusAndPrint(List<String> components) {
        ComponentsLoaderImpl componentsLoader = componentsLoader(paths.componentsPath(), components, oc);
        List<JobComponent> jobComponents = componentsLoader.load(JobComponent.class);
        List<DeploymentComponent> deploymentComponents = componentsLoader.load(DeploymentComponent.class);
        return statusPrinter(jobComponents, deploymentComponents, healthChecker, printer).checkStatusAndPrint();
    }
}

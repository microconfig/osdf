package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.components.JobComponent;
import io.microconfig.osdf.components.checker.HealthChecker;
import io.microconfig.osdf.components.info.DeploymentInfo;
import io.microconfig.osdf.components.info.JobInfo;
import io.microconfig.osdf.components.loader.ComponentsLoaderImpl;
import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.OpenShiftProject;
import io.microconfig.osdf.printer.ColumnPrinter;
import io.microconfig.utils.Logger;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.components.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.istio.VirtualService.virtualService;
import static io.microconfig.osdf.openshift.OpenShiftProject.create;

@RequiredArgsConstructor
public class StatusCommand {
    private final OSDFPaths paths;
    private final OCExecutor oc;
    private final HealthChecker healthChecker;
    private final ColumnPrinter printer;

    public void run(List<String> components) {
        try (OpenShiftProject ignored = create(paths, oc).connect()) {
            printer.addColumns("COMPONENT", "VERSION", "TRAFFIC", "STATUS", "REPLICAS");
            ComponentsLoaderImpl componentsLoader = componentsLoader(paths.componentsPath(), components, oc);

            componentsLoader.load(JobComponent.class).forEach(component -> addJobInfo(printer, component));
            addDeploymentComponents(printer, componentsLoader.load(DeploymentComponent.class));

            printer.print();
        }
    }

    private void addJobInfo(ColumnPrinter printer, JobComponent component) {
        JobInfo info = component.info();
        printer.addRow(component.getName(), component.getVersion(), "-", info.getStatus().toString(), "job");
    }

    private void addDeploymentComponents(ColumnPrinter printer, List<DeploymentComponent> components) {
        components.parallelStream().forEach(component -> addDeploymentComponent(printer, component));
    }

    private void addDeploymentComponent(ColumnPrinter printer, DeploymentComponent component) {
        List<DeploymentComponent> deployedComponents = component.getDeployedComponents();
        if (deployedComponents.isEmpty()) {
            printer.addRow(component.getName(),component.getVersion(), "-", "NOT_DEPLOYED", "-");
            return;
        }

        ColumnPrinter localPrinter = printer.newPrinter();
        addComponentHeader(component, localPrinter, deployedComponents);
        deployedComponents.parallelStream().forEach(c -> addDeployedComponent(localPrinter, c));
        printer.addRows(localPrinter);
    }

    private void addDeployedComponent(ColumnPrinter printer, DeploymentComponent component) {
        DeploymentInfo info = component.info(healthChecker);
        printer.addRow(Logger::info, "",
                component.getVersion(),
                virtualService(oc, component).getTrafficStatus(),
                info.getStatus().toString(),
                info.getAvailableReplicas() + "/" + info.getReplicas());
    }

    private void addComponentHeader(DeploymentComponent component, ColumnPrinter localPrinter, List<DeploymentComponent> deployedComponents) {
        boolean deployed = deployedComponents.stream().map(DeploymentComponent::getVersion).anyMatch(v -> v.equals(component.getVersion()));
        String fullName = component.getName() + "{" + component.getVersion() + "}";
        if (!deployed) {
            fullName += "(not deployed)";
        }
        localPrinter.addRow(fullName, "", "", "", "");
    }
}

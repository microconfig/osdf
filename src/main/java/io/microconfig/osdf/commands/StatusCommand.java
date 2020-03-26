package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.components.JobComponent;
import io.microconfig.osdf.components.info.DeploymentInfo;
import io.microconfig.osdf.components.info.JobInfo;
import io.microconfig.osdf.components.loader.ComponentsLoaderImpl;
import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.microconfig.properties.HealthCheckProperties;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.OpenShiftProject;
import io.microconfig.osdf.printer.ColumnPrinter;
import io.microconfig.osdf.state.OSDFState;
import io.microconfig.utils.Logger;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.components.checker.LogHealthChecker.logHealthChecker;
import static io.microconfig.osdf.components.info.DeploymentInfo.info;
import static io.microconfig.osdf.components.info.DeploymentStatus.NOT_FOUND;
import static io.microconfig.osdf.components.info.DeploymentStatus.UNKNOWN;
import static io.microconfig.osdf.components.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.microconfig.properties.HealthCheckProperties.properties;
import static io.microconfig.osdf.microconfig.properties.PropertyGetter.propertyGetter;
import static io.microconfig.osdf.openshift.OpenShiftProject.create;
import static io.microconfig.osdf.printer.ColumnPrinter.printer;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class StatusCommand {
    private final OSDFPaths paths;
    private final OCExecutor oc;

    public void run(List<String> components) {
        try (OpenShiftProject ignored = create(paths, oc).connect()) {
            ColumnPrinter printer = printer("COMPONENT", "STATUS", "REPLICAS", "VERSION", "CONFIGS");
            ComponentsLoaderImpl componentsLoader = componentsLoader(paths.componentsPath(), components, oc);
            String env = OSDFState.fromFile(paths.stateSavePath()).getEnv();
            HealthCheckProperties properties = properties(propertyGetter(env, paths.configPath()));

            componentsLoader.load(JobComponent.class).forEach(component -> addJobInfo(printer, component));
            addDeploymentComponents(printer, properties, componentsLoader.load(DeploymentComponent.class));

            printer.print();
        }
    }

    private void addJobInfo(ColumnPrinter printer, JobComponent component) {
        JobInfo info = component.info();
        printer.addRow(component.getName(), info.getStatus().toString(), "patcher", info.getProjectVersion(), info.getConfigVersion());
    }

    private void addDeploymentComponents(ColumnPrinter printer, HealthCheckProperties properties, List<DeploymentComponent> components) {
        List<DeploymentInfo> infos = components
                .parallelStream()
                .map(component -> info(component, logHealthChecker(component, properties)))
                .collect(toList());

        for (int i = 0; i < infos.size(); i++) {
            addDeploymentComponent(printer, components.get(i), infos.get(i));
        }
    }

    private void addDeploymentComponent(ColumnPrinter printer, DeploymentComponent component, DeploymentInfo info) {
        String replicasInfo = (info.getStatus() == UNKNOWN || info.getStatus() == NOT_FOUND) ? "?/?" : info.getAvailableReplicas() + "/" + info.getReplicas();
        printer.addRow(component.getName(), info.getStatus().toString(), replicasInfo, info.getProjectVersion(), info.getConfigVersion());
        for (int i = 0; i < info.getPods().size(); i++) {
            printer.addRow(Logger::info, " - " + info.getPods().get(i).getName(), info.getPodsHealth().get(i) ? "OK" : "NOT_READY", "", "", "");
        }
    }
}

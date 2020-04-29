package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.OpenShiftProject;
import io.microconfig.osdf.openshift.Pod;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.components.DeploymentComponent.component;
import static io.microconfig.osdf.openshift.OpenShiftProject.create;
import static io.microconfig.utils.Logger.error;

@RequiredArgsConstructor
public class LogsCommand {
    private final OSDFPaths paths;
    private final OCExecutor oc;


    public void show(String componentName, String podName) {
        DeploymentComponent component = component(componentName, paths.componentsPath(), oc);

        try (OpenShiftProject ignored = create(paths, oc).connect()) {
            Pod pod = component.pod(podName == null ? "0" : podName);
            if (pod == null) {
                error("Pod " + podName + " not found");
                return;
            }
            pod.logs();
        }
    }
}

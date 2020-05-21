package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.openshift.OpenShiftCLI;
import io.microconfig.osdf.openshift.Pod;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.components.DeploymentComponent.component;
import static io.microconfig.osdf.openshift.Pod.fromPods;
import static io.microconfig.utils.Logger.error;

@RequiredArgsConstructor
public class LogsCommand {
    private final OSDFPaths paths;
    private final OpenShiftCLI oc;


    public void show(String componentName, String podName) {
        DeploymentComponent component = component(componentName, paths, oc);

        Pod pod = fromPods(component.pods(), podName);
        if (pod == null) {
            error("Pod " + podName + " not found");
            return;
        }
        pod.logs();
    }
}

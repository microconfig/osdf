package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.OpenShiftProject;
import io.microconfig.osdf.openshift.Pod;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.components.DeploymentComponent.component;
import static io.microconfig.osdf.openshift.OpenShiftProject.create;
import static io.microconfig.utils.Logger.announce;
import static io.microconfig.utils.Logger.warn;

@RequiredArgsConstructor
public class DeletePodCommand {
    private final OSDFPaths paths;
    private final OCExecutor oc;


    public void delete(String componentName, List<String> podNames) {
        try (OpenShiftProject ignored = create(paths, oc).connect()) {
            podNames.forEach(podName -> deletePods(component(componentName, paths.componentsPath(), oc), podNames));
        }
    }

    private void deletePods(DeploymentComponent component, List<String> podNames) {
        List<Pod> pods = component.pods();
        for (String podName : podNames) {
            Pod pod = component.pod(pods, podName);
            if (pod == null) {
                warn("Pod " + podName + " not found");
                continue;
            }
            deletePod(pod);
        }
    }

    private void deletePod(Pod pod) {
        pod.delete();
        announce("Deleted pod " + pod.getName());
    }
}

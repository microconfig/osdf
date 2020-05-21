package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.openshift.OpenShiftCLI;
import io.microconfig.osdf.openshift.Pod;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.components.DeploymentComponent.component;
import static io.microconfig.osdf.openshift.Pod.fromPods;
import static io.microconfig.utils.Logger.announce;

@RequiredArgsConstructor
public class DeletePodCommand {
    private final OSDFPaths paths;
    private final OpenShiftCLI oc;


    public void delete(String componentName, List<String> podNames) {
        podNames.forEach(podName -> deletePods(component(componentName, paths, oc), podNames));
    }

    private void deletePods(DeploymentComponent component, List<String> podNames) {
        List<Pod> pods = component.pods();
        podNames.stream()
                .map(podName -> fromPods(pods, podName))
                .forEach(this::deletePod);
    }

    private void deletePod(Pod pod) {
        pod.delete();
        announce("Deleted pod " + pod.getName());
    }
}

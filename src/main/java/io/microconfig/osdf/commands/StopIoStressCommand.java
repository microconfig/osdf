package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.OpenShiftProject;
import io.microconfig.osdf.openshift.Pod;
import lombok.AllArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.components.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.openshift.OpenShiftProject.create;

@AllArgsConstructor
public class StopIoStressCommand {
    private final OSDFPaths paths;
    private final OCExecutor oc;

    public void run(List<String> components) {
        try (OpenShiftProject ignored = create(paths, oc).connect()) {

            componentsLoader(paths, components, oc)
                    .load(DeploymentComponent.class)
                    .forEach(this::stopStress);
        }
    }

    private void stopStress(DeploymentComponent component) {
        component.pods()
                .stream()
                .filter(Pod::checkStressContainer)
                .forEach(this::killSidecar);
    }

    private void killSidecar(Pod pod) {
        oc.execute("oc exec " + pod.getName() + " -c stress-sidecar -- /bin/sh -c \"kill 1\"");
    }

}


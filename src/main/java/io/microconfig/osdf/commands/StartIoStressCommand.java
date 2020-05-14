package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.OpenShiftProject;
import io.microconfig.osdf.openshift.Pod;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Random;

import static io.microconfig.osdf.components.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.openshift.OpenShiftProject.create;

@RequiredArgsConstructor
public class StartIoStressCommand {
    private final OSDFPaths paths;
    private final OCExecutor oc;
    private final Integer duration;
    private final Integer chaosSeverity;
    private final Random r = new Random();

    public void run(List<String> components) {
        try (OpenShiftProject ignored = create(paths, oc).connect()) {
            componentsLoader(paths, components, oc)
                    .load(DeploymentComponent.class)
                    .forEach(component -> printPods(component, chaosSeverity));
        }
    }

    private void printPods(DeploymentComponent component, int chaosChance) {
        component.pods()
                .stream()
                .filter(Pod::checkStressContainer)
                .filter(pod -> r.nextInt(100) <= chaosChance)
                .forEach(this::startStress);
    }

    private void startStress(Pod pod) {
        oc.executeAndForget("oc exec " + pod.getName() + " -c stress-sidecar -- /bin/sh -c \"cd /fs; stress-ng --all 0 -t "
                + duration + "s --class io\"");
    }

}

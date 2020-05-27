package io.microconfig.osdf.chaos.chaosRunners;

import io.microconfig.osdf.chaos.ChaosSet;
import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.OpenShiftProject;
import io.microconfig.osdf.openshift.Pod;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Random;

import static io.microconfig.osdf.components.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.openshift.OpenShiftProject.create;
import static io.microconfig.utils.Logger.announce;

@AllArgsConstructor
public class IOChaosRunner implements ChaosRunner {
    private final OSDFPaths paths;
    private final OCExecutor oc;
    private final Random r = new Random();

    public static ChaosRunner ioChaosRunner(OSDFPaths paths, OCExecutor oc) {
        return new IOChaosRunner(paths, oc);
    }

    @Override
    public void run(List<String> components, ChaosSet chaosSet, Integer severity, Integer duration) {
        try (OpenShiftProject ignored = create(paths, oc).connect()) {
            componentsLoader(paths, components, oc)
                    .load(DeploymentComponent.class)
                    .forEach(component -> startStress(component, severity, chaosSet.getIoStressTimeout()));
        }
    }

    @Override
    public void stop(List<String> components) {
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
        announce("IO chaos stopped in " + pod.getName());
    }

    private void startStress(DeploymentComponent component, int chaosChance, int timeout) {
        component.pods()
                .stream()
                .filter(Pod::checkStressContainer)
                .filter(pod -> r.nextInt(100) <= chaosChance)
                .forEach(pod -> {
                    oc.executeAndForget("oc exec " + pod.getName() + " -c stress-sidecar -- /bin/sh -c \"cd /fs; stress-ng --all 0 -t "
                            + timeout + "s --class io\"");
                    announce("IO chaos started in " + pod.getName());
                });
    }
}

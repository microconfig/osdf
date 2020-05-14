package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.OpenShiftProject;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Random;

import static io.microconfig.osdf.components.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.openshift.OpenShiftProject.create;
import static io.microconfig.utils.Logger.announce;

@AllArgsConstructor
public class KillPodsCommand {
    private final OSDFPaths paths;
    private final OCExecutor oc;
    private final Integer chaosSeverity;
    private final Random r = new Random();

    public void run(List<String> components) {
        try (OpenShiftProject ignored = create(paths, oc).connect()) {
            componentsLoader(paths, components, oc)
                    .load(DeploymentComponent.class)
                    .forEach(component -> killPods(component, chaosSeverity));
        }
    }

    private void killPods(DeploymentComponent component, int chaosChance) {
        component.pods()
                .stream()
                .filter(pod -> r.nextInt(100) <= chaosChance)
                .forEach(pod -> {
                    pod.delete();
                    announce(pod.getName() + " killed.");
                });
    }
}

package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.components.loader.ComponentsLoaderImpl;
import io.microconfig.osdf.deployers.Deployer;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.OpenShiftProject;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.components.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.openshift.OpenShiftProject.create;

@RequiredArgsConstructor
public class NetworkChaosCommand {
    private final OSDFPaths paths;
    private final OCExecutor oc;
    private final Deployer deployer;

    public void run(List<String> components) {
        ComponentsLoaderImpl componentsLoader = componentsLoader(paths, components, oc);
        try (OpenShiftProject ignored = create(paths, oc).connect()) {
            deployChaos(componentsLoader);
        }
    }

    private void deployChaos(ComponentsLoaderImpl componentsLoader) {
        componentsLoader.load(DeploymentComponent.class).forEach(deployer::deploy);
    }
}

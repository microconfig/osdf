package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.openshift.OpenShiftCLI;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.components.loader.ComponentsLoaderImpl.componentsLoader;

@RequiredArgsConstructor
public class StopCommand {
    private final OSDFPaths paths;
    private final OpenShiftCLI oc;

    public void run(List<String> components) {
        componentsLoader(paths, components, oc)
                .load(DeploymentComponent.class)
                .forEach(DeploymentComponent::stop);
    }
}

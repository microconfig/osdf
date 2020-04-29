package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.OpenShiftProject;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.components.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.openshift.OpenShiftProject.create;

@RequiredArgsConstructor
public class DeleteCommand {
    private final OSDFPaths paths;
    private final OCExecutor oc;

    public void delete(List<String> components) {
        try (OpenShiftProject ignored = create(paths, oc).connect()) {
            componentsLoader(paths.componentsPath(), components, oc)
                    .load(DeploymentComponent.class)
                    .forEach(DeploymentComponent::delete);
        }
    }
}

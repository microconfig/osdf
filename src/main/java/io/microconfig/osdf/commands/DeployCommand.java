package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.AbstractOpenShiftComponent;
import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.components.JobComponent;
import io.microconfig.osdf.components.loader.ComponentsLoaderImpl;
import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.OpenShiftProject;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.components.info.JobStatus.SUCCEEDED;
import static io.microconfig.osdf.components.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.openshift.OpenShiftProject.create;
import static io.microconfig.utils.Logger.announce;
import static io.microconfig.utils.Logger.error;

@RequiredArgsConstructor
public class DeployCommand {
    private final OSDFPaths paths;
    private final OCExecutor oc;


    public void run(List<String> components) {
        ComponentsLoaderImpl componentsLoader = componentsLoader(paths.componentsPath(), components, oc);

        try (OpenShiftProject ignored = create(paths, oc).connect()) {
            deployJobs(componentsLoader);
            deployDeployments(componentsLoader);
        }
    }

    private void deployDeployments(ComponentsLoaderImpl componentsLoader) {
        componentsLoader.load(DeploymentComponent.class).forEach(component -> {
            upload(component);
            announce("Loaded component " + component);
        });
    }

    private void deployJobs(ComponentsLoaderImpl componentsLoader) {
        componentsLoader.load(JobComponent.class).forEach(component -> {
            if (component.exists() && component.status() != SUCCEEDED) {
                component.delete();
            }
            upload(component);

            if (!component.waitUntilCompleted()) {
                error("Job " + component.getName() + " failed");
                throw new RuntimeException("Job failed");
            }

            announce("Completed job " + component);
        });
    }

    private void upload(AbstractOpenShiftComponent component) {
        component.deleteOldResourcesFromOpenShift();
        component.createConfigMap();
        component.upload();
    }

}

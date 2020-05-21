package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.components.JobComponent;
import io.microconfig.osdf.components.checker.SuccessfulDeploymentChecker;
import io.microconfig.osdf.components.loader.ComponentsLoaderImpl;
import io.microconfig.osdf.deployers.Deployer;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.exceptions.StatusCodeException;
import io.microconfig.osdf.openshift.OpenShiftCLI;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.components.checker.SuccessfulDeploymentChecker.successfulDeploymentChecker;
import static io.microconfig.osdf.components.info.JobStatus.SUCCEEDED;
import static io.microconfig.osdf.components.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.resources.ResourceVersionInserter.resourceVersionInserter;
import static io.microconfig.utils.Logger.announce;
import static io.microconfig.utils.Logger.error;

@RequiredArgsConstructor
public class DeployCommand {
    private final OSDFPaths paths;
    private final OpenShiftCLI oc;
    private final Deployer deployer;
    private final boolean wait;

    public void run(List<String> components) {
        ComponentsLoaderImpl componentsLoader = componentsLoader(paths, components, oc);

        List<DeploymentComponent> deploymentComponents = componentsLoader.load(DeploymentComponent.class);
        List<JobComponent> jobComponents = componentsLoader.load(JobComponent.class);

        deployJobs(jobComponents);
        deployDeployments(deploymentComponents);
        printDeploymentStatus(deploymentComponents);
    }

    private void deployDeployments(List<DeploymentComponent> deploymentComponents) {
        deploymentComponents.forEach(component -> {
            insertVersion(component);
            deployer.deploy(component);
            announce("Loaded component " + component);
        });
    }

    private void insertVersion(DeploymentComponent component) {
        resourceVersionInserter(component.getConfigDir(), component.isPrimary() ? null : component.getVersion()).insert();
    }

    private void deployJobs(List<JobComponent> jobComponents) {
        jobComponents.forEach(component -> {
            if (component.exists() && component.status() == SUCCEEDED) return;

            component.delete();
            component.createConfigMap();
            component.upload();

            if (!component.waitUntilCompleted()) {
                throw new OSDFException("Job " + component.getName() + " failed");
            }

            announce("Completed job " + component);
        });
    }

    private void printDeploymentStatus(List<DeploymentComponent> deploymentComponents) {
        if (checkIfSuccessful(deploymentComponents)) {
            announce("OK");
        } else {
            error("Some components didn't start in time or had failures");
            throw new StatusCodeException(1);
        }
    }

    private boolean checkIfSuccessful(List<DeploymentComponent> deploymentComponents) {
        if (wait) {
            SuccessfulDeploymentChecker checker = successfulDeploymentChecker();
            return deploymentComponents.parallelStream().allMatch(component -> checkDeployment(checker, component));
        }
        return true;
    }

    private boolean checkDeployment(SuccessfulDeploymentChecker checker, DeploymentComponent component) {
        boolean status = checker.check(component);
        if (!status) error("Component " + component.getName() + " failed");
        return status;
    }
}

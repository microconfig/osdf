package io.osdf.actions.management.deploy.smart.checker;

import io.osdf.actions.management.deploy.smart.hash.ResourcesHashComputer;
import io.osdf.core.application.core.Application;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.cluster.resource.ClusterResource;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import static io.osdf.actions.management.deploy.smart.hash.ResourcesHashComputer.resourcesHashComputer;
import static io.osdf.core.application.service.ServiceApplication.serviceApplication;

@RequiredArgsConstructor
public class UpToDateServiceChecker implements UpToDateChecker {
    private final ClusterCli cli;
    private final ResourcesHashComputer resourcesHashComputer = resourcesHashComputer();

    public static UpToDateServiceChecker upToDateDeploymentChecker(ClusterCli cli) {
        return new UpToDateServiceChecker(cli);
    }

    @Override
    public boolean check(Application app) {
        ServiceApplication service = serviceApplication(app);

        if (!service.exists()) return false;
        ClusterResource deploymentResource = service.deployment().toResource();
        if (!deploymentResource.exists(cli)) return false;
        String configHash = deploymentResource.label("configHash", cli);
        return configHash.equals(resourcesHashComputer.currentHash(service.files()));
    }

}

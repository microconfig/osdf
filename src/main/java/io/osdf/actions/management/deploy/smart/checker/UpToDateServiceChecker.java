package io.osdf.actions.management.deploy.smart.checker;

import io.osdf.actions.management.deploy.smart.hash.ResourcesHashComputer;
import io.osdf.core.application.core.Application;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.cluster.deployment.ClusterDeployment;
import io.osdf.core.cluster.resource.ClusterResource;
import io.osdf.core.cluster.resource.properties.ResourceProperties;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static io.osdf.actions.management.deploy.smart.hash.ResourcesHashComputer.resourcesHashComputer;
import static io.osdf.core.application.service.ServiceApplication.serviceApplication;
import static io.osdf.core.cluster.resource.properties.ResourceProperties.resourceProperties;
import static java.util.Map.of;

@RequiredArgsConstructor
public class UpToDateServiceChecker implements UpToDateChecker {
    private final ClusterCli cli;
    private final ResourcesHashComputer resourcesHashComputer;

    public static UpToDateServiceChecker upToDateDeploymentChecker(ClusterCli cli) {
        return new UpToDateServiceChecker(cli, resourcesHashComputer());
    }

    @Override
    public boolean check(Application app) {
        ServiceApplication service = serviceApplication(app);

        Optional<ClusterDeployment> deployment = service.deployment();
        if (deployment.isEmpty()) return false;

        ClusterResource deploymentResource = deployment.get().toResource();
        Optional<ResourceProperties> properties = resourceProperties(cli, deploymentResource, of("hash", "metadata.labels.configHash"));
        if (properties.isEmpty()) return false;

        String hash = properties.get().get("hash");
        String currentHash = resourcesHashComputer.currentHash(service.files());
        return hash.equals(currentHash);
    }

}

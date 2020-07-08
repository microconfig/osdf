package io.osdf.actions.management.deploy.smart;

import io.osdf.actions.management.deploy.smart.hash.ResourcesHashComputer;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.cluster.resource.ClusterResource;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.actions.management.deploy.smart.hash.ResourcesHashComputer.resourcesHashComputer;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class UpToDateDeploymentFilter {
    private final ClusterCli cli;
    private final ResourcesHashComputer resourcesHashComputer = resourcesHashComputer();

    public static UpToDateDeploymentFilter upToDateDeploymentFilter(ClusterCli cli) {
        return new UpToDateDeploymentFilter(cli);
    }

    public List<ServiceApplication> filter(List<ServiceApplication> services) {
        return services.parallelStream()
                .filter(service -> !isUpToDate(service))
                .collect(toUnmodifiableList());
    }

    private boolean isUpToDate(ServiceApplication service) {
        if (!service.exists()) return false;
        ClusterResource deploymentResource = service.deployment().toResource();
        if (!deploymentResource.exists(cli)) return false;
        String configHash = deploymentResource.label("configHash", cli);
        return configHash.equals(resourcesHashComputer.currentHash(service.files()));
    }

}

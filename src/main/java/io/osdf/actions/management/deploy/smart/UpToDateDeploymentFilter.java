package io.osdf.actions.management.deploy.smart;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.actions.management.deploy.smart.hash.ResourceHash;
import io.osdf.core.service.core.deployment.ServiceDeployment;
import io.osdf.core.service.core.deployment.pack.ServiceDeployPack;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.actions.info.info.deployment.DeploymentStatus.NOT_FOUND;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class UpToDateDeploymentFilter {
    private final ClusterCli cli;

    public static UpToDateDeploymentFilter upToDateDeploymentFilter(ClusterCli cli) {
        return new UpToDateDeploymentFilter(cli);
    }

    public List<ServiceDeployPack> filter(List<ServiceDeployPack> services, ResourceHash resourceHash) {
        return services.parallelStream()
                .filter(service -> !isUpToDate(service, resourceHash))
                .collect(toUnmodifiableList());
    }

    public boolean isUpToDate(ServiceDeployPack deployPack, ResourceHash resourceHash) {
        return totalHashIsRecent(resourceHash.currentHash(deployPack.files()), deployPack.deployment());
    }

    private boolean totalHashIsRecent(String hash, ServiceDeployment deployment) {
        if (deployment.info().status() == NOT_FOUND) return false;
        String configHash = deployment.toResource().label(cli, "configHash");
        return configHash.equals(hash);
    }
}

package io.microconfig.osdf.service.deployment.tools;

import io.cluster.old.cluster.cli.ClusterCLI;
import io.microconfig.osdf.resources.ResourceHash;
import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.deployment.pack.ServiceDeployPack;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.service.deployment.info.DeploymentStatus.NOT_FOUND;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class UpToDateDeploymentFilter {
    private final ClusterCLI cli;

    public static UpToDateDeploymentFilter upToDateDeploymentFilter(ClusterCLI cli) {
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

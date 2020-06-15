package io.microconfig.osdf.service.deployment.tools;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.deployment.pack.ServiceDeployPack;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.resources.DeploymentHashInserter.deploymentHashInserter;
import static io.microconfig.osdf.service.deployment.checkers.image.ImageVersionChecker.imageVersionChecker;
import static io.microconfig.osdf.service.deployment.info.DeploymentStatus.NOT_FOUND;
import static io.microconfig.osdf.service.deployment.tools.DeploymentRestarter.deploymentRestarter;
import static io.microconfig.utils.Logger.info;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class DeployRequiredFilter {
    private final OSDFPaths paths;
    private final ClusterCLI cli;

    public static DeployRequiredFilter deployRequiredFilter(OSDFPaths paths, ClusterCLI cli) {
        return new DeployRequiredFilter(paths, cli);
    }

    public List<ServiceDeployPack> filter(List<ServiceDeployPack> services) {
        return services.parallelStream()
                .filter(service -> !isUpToDate(service))
                .collect(toUnmodifiableList());
    }

    public boolean isUpToDate(ServiceDeployPack deployPack) {
        String hash = deploymentHashInserter().insert(deployPack.files());
        if (!totalHashIsRecent(hash, deployPack.deployment())) return false;

        if (!imageVersionChecker(deployPack.deployment(), deployPack.files(), paths).isLatest()) {
            info("Restarting " + deployPack.service().name() + " to pull new image");
            deploymentRestarter().restart(deployPack.deployment(), deployPack.files());
        }
        return true;
    }

    private boolean totalHashIsRecent(String hash, ServiceDeployment deployment) {
        if (deployment.info().status() == NOT_FOUND) return false;
        String configHash = deployment.toResource().label(cli, "configHash");
        return configHash.equals(hash);
    }
}

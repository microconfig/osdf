package io.microconfig.osdf.service.deployment.tools;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.cluster.resource.totalhash.TotalHashesStorage;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.deployment.pack.ServiceDeployPack;
import io.microconfig.osdf.service.files.ServiceFiles;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.cluster.resource.totalhash.TotalHashComputer.totalHashComputer;
import static io.microconfig.osdf.cluster.resource.totalhash.TotalHashesStorage.totalHashesStorage;
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
        TotalHashesStorage totalHashesStorage = totalHashesStorage(cli);
        List<ServiceDeployPack> requiredPacks = services.parallelStream()
                .filter(service -> !isUpToDate(service, totalHashesStorage))
                .collect(toUnmodifiableList());
        totalHashesStorage.save();
        return requiredPacks;
    }

    public boolean isUpToDate(ServiceDeployPack deployPack, TotalHashesStorage totalHashesStorage) {
        if (!totalHashIsRecent(totalHashesStorage, deployPack.deployment(), deployPack.files())) return false;

        if (!imageVersionChecker(deployPack.deployment(), deployPack.files(), paths).isLatest()) {
            info("Restarting " + deployPack.service().name() + " to pull new image");
            deploymentRestarter().restart(deployPack.deployment(), deployPack.files());
        }
        return true;
    }

    private boolean totalHashIsRecent(TotalHashesStorage totalHashesStorage, ServiceDeployment deployment, ServiceFiles files) {
        if (deployment.info().status() == NOT_FOUND) return false;

        String totalHash = totalHashComputer(files).compute();
        if (totalHashesStorage.contains(deployment.name(), totalHash)) return true;

        totalHashesStorage.setHash(deployment.name(), totalHash);
        return false;
    }
}

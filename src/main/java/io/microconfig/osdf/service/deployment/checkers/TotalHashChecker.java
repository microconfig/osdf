package io.microconfig.osdf.service.deployment.checkers;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.cluster.resource.totalhash.TotalHashesStorage;
import io.microconfig.osdf.service.deployment.checkers.image.ImageVersionChecker;
import io.microconfig.osdf.service.files.ServiceFiles;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.service.deployment.info.DeploymentStatus.NOT_FOUND;
import static io.microconfig.osdf.service.deployment.tools.DeploymentRestarter.deploymentRestarter;
import static io.microconfig.osdf.cluster.resource.totalhash.TotalHashComputer.totalHashComputer;
import static io.microconfig.osdf.cluster.resource.totalhash.TotalHashesStorage.totalHashesStorage;
import static io.microconfig.utils.Logger.info;

@RequiredArgsConstructor
public class TotalHashChecker {
    private final OSDFPaths paths;
    private final ClusterCLI cli;

    public static TotalHashChecker totalHashChecker(OSDFPaths paths, ClusterCLI cli) {
        return new TotalHashChecker(paths, cli);
    }

    public boolean check(ServiceDeployment deployment, ServiceFiles files) {
        if (!totalHashIsRecent(deployment, files)) return false;

        if (ImageVersionChecker.imageVersionChecker(deployment, files, paths).isLatest()) {
            info("Up-to-date");
        } else {
            info("Restarting to pull new image");
            deploymentRestarter().restart(deployment, files);
        }
        return true;
    }

    private boolean totalHashIsRecent(ServiceDeployment deployment, ServiceFiles files) {
        if (deployment.info().status() == NOT_FOUND) return false;

        String totalHash = totalHashComputer(files).compute();
        TotalHashesStorage totalHashesStorage = totalHashesStorage(cli);
        if (totalHashesStorage.contains(deployment.name(), totalHash)) return true;

        info("Hash " + totalHash + " is not found for " + deployment.name());
        totalHashesStorage.setHash(deployment.name(), totalHash);
        totalHashesStorage.save();
        return false;
    }
}

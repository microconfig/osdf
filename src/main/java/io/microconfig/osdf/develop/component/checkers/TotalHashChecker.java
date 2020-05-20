package io.microconfig.osdf.develop.component.checkers;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.develop.component.ClusterDeployment;
import io.microconfig.osdf.develop.component.ComponentFiles;
import io.microconfig.osdf.develop.resources.TotalHashesStorage;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.components.info.DeploymentStatus.NOT_FOUND;
import static io.microconfig.osdf.develop.component.checkers.NewImageVersionChecker.imageVersionChecker;
import static io.microconfig.osdf.develop.resources.TotalHashComputer.totalHashComputer;
import static io.microconfig.osdf.develop.resources.TotalHashesStorage.totalHashesStorage;
import static io.microconfig.utils.Logger.info;

@RequiredArgsConstructor
public class TotalHashChecker {
    private final OSDFPaths paths;
    private final ClusterCLI cli;

    public static TotalHashChecker totalHashChecker(OSDFPaths paths, ClusterCLI cli) {
        return new TotalHashChecker(paths, cli);
    }

    public boolean check(ClusterDeployment deployment, ComponentFiles files) {
        if (!totalHashIsRecent(deployment, files)) return false;

        if (imageVersionChecker(deployment, files, paths).isLatest()) {
            info("Up-to-date");
        } else {
            info("Restarting to pull new image");
            deployment.restart();
        }
        return true;
    }

    private boolean totalHashIsRecent(ClusterDeployment deployment, ComponentFiles files) {
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

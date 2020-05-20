package io.microconfig.osdf.develop.deployers;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.develop.component.ClusterComponent;
import io.microconfig.osdf.develop.component.ClusterDeployment;
import io.microconfig.osdf.develop.component.ComponentFiles;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.develop.component.checkers.NewImageVersionChecker.imageVersionChecker;
import static io.microconfig.osdf.develop.component.checkers.TotalHashChecker.totalHashChecker;
import static io.microconfig.osdf.develop.resources.ResourceCleaner.resourceCleaner;
import static io.microconfig.utils.Logger.info;

@RequiredArgsConstructor
public class DefaultClusterDeployer implements ClusterDeployer {
    private final ClusterCLI cli;
    private final OSDFPaths paths;

    public static DefaultClusterDeployer defaultClusterDeployer(ClusterCLI cli, OSDFPaths paths) {
        return new DefaultClusterDeployer(cli, paths);
    }

    @Override
    public void deploy(ClusterComponent component, ClusterDeployment deployment, ComponentFiles files) {
        if (totalHashChecker(paths, cli).check(deployment, files)) return;

        resourceCleaner(cli).cleanOld(files.resources(), component.resources());
        boolean configMapUpdated = deployment.createConfigMap(files.configs());
        if (uploadResourcesAndCheckHash(component, deployment, files)) return;

        restartIfNecessary(deployment, files, configMapUpdated);
    }

    private void restartIfNecessary(ClusterDeployment deployment, ComponentFiles files, boolean configMapUpdated) {
        boolean latestImage = imageVersionChecker(deployment, files, paths).isLatest();
        if (configMapUpdated) info("Application configs were updated");
        if (!latestImage) info("Component doesn't have latest image");
        if (configMapUpdated || !latestImage) {
            info("Restarting");
            deployment.restart();
        } else {
            info("Up-to-date");
        }
    }

    private boolean uploadResourcesAndCheckHash(ClusterComponent component, ClusterDeployment deployment, ComponentFiles files) {
        String currentHash = deployment.info().hash();
        component.upload(files.resources());
        String deployedHash = deployment.info().hash();
        return deployedHash.equals(currentHash);
    }
}

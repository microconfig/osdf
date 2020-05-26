package io.microconfig.osdf.develop.deployers;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.develop.service.ClusterService;
import io.microconfig.osdf.develop.service.deployment.ServiceDeployment;
import io.microconfig.osdf.develop.service.files.ServiceFiles;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.develop.cluster.resource.tools.ResourceCleaner.resourceCleaner;
import static io.microconfig.osdf.develop.service.deployment.checkers.NewImageVersionChecker.imageVersionChecker;
import static io.microconfig.osdf.develop.service.deployment.checkers.TotalHashChecker.totalHashChecker;
import static io.microconfig.osdf.develop.service.deployment.tools.DeploymentRestarter.deploymentRestarter;
import static io.microconfig.utils.Logger.info;

@RequiredArgsConstructor
public class DefaultServiceDeployer implements ServiceDeployer {
    private final ClusterCLI cli;
    private final OSDFPaths paths;

    public static DefaultServiceDeployer defaultClusterDeployer(ClusterCLI cli, OSDFPaths paths) {
        return new DefaultServiceDeployer(cli, paths);
    }

    @Override
    public void deploy(ClusterService service, ServiceDeployment deployment, ServiceFiles files) {
        info("Deploying " + service.name());
        if (totalHashChecker(paths, cli).check(deployment, files)) return;

        resourceCleaner(cli).cleanOld(files.resources(), service.resources());
        boolean configMapUpdated = deployment.createConfigMap(files.configs());
        if (uploadResourcesAndCheckHash(service, deployment, files)) return;

        restartIfNecessary(deployment, files, configMapUpdated);
    }

    private void restartIfNecessary(ServiceDeployment deployment, ServiceFiles files, boolean configMapUpdated) {
        boolean latestImage = imageVersionChecker(deployment, files, paths).isLatest();
        if (configMapUpdated) info("Application configs were updated");
        if (!latestImage) info("Component doesn't have latest image");
        if (configMapUpdated || !latestImage) {
            info("Restarting");
            deploymentRestarter().restart(deployment, files);
        } else {
            info("Up-to-date");
        }
    }

    private boolean uploadResourcesAndCheckHash(ClusterService component, ServiceDeployment deployment, ServiceFiles files) {
        String currentHash = deployment.info().hash();
        component.upload(files.resources());
        String deployedHash = deployment.info().hash();
        return deployedHash.equals(currentHash);
    }
}

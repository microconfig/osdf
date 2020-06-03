package io.microconfig.osdf.deployers;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.deployers.hooks.DeployHook;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.service.ClusterService;
import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.files.ServiceFiles;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.cluster.resource.tools.ResourceCleaner.resourceCleaner;
import static io.microconfig.osdf.deployers.hooks.EmptyHook.emptyHook;
import static io.microconfig.osdf.service.deployment.checkers.image.ImageVersionChecker.imageVersionChecker;
import static io.microconfig.osdf.service.deployment.tools.DeploymentRestarter.deploymentRestarter;
import static io.microconfig.utils.Logger.info;

@RequiredArgsConstructor
public class BaseServiceDeployer implements ServiceDeployer {
    private final ClusterCLI cli;
    private final OSDFPaths paths;
    private final DeployHook deployHook;

    public static BaseServiceDeployer baseServiceDeployer(ClusterCLI cli, OSDFPaths paths) {
        return new BaseServiceDeployer(cli, paths, emptyHook());
    }

    public static BaseServiceDeployer baseServiceDeployer(ClusterCLI cli, OSDFPaths paths, DeployHook hook) {
        return new BaseServiceDeployer(cli, paths, hook);
    }

    @Override
    public void deploy(ClusterService service, ServiceDeployment deployment, ServiceFiles files) {
        info("Deploying " + service.name());

        resourceCleaner(cli).cleanOld(files.resources(), service.resources());
        boolean configMapUpdated = deployment.createConfigMap(files.configs());
        if (!uploadResourcesAndCheckHashIsSame(service, deployment, files)) return;

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

    private boolean uploadResourcesAndCheckHashIsSame(ClusterService service, ServiceDeployment deployment, ServiceFiles files) {
        String currentHash = deployment.info().hash();
        service.upload(files.resources());
        deployHook.call(service, deployment, files);
        String deployedHash = deployment.info().hash();
        return deployedHash.equals(currentHash);
    }
}

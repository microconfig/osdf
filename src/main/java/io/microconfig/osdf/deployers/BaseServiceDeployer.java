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
        deployment.createConfigMap(files.configs());
        uploadResourcesAndCheckHashIsSame(service, deployment, files);
    }

    private void uploadResourcesAndCheckHashIsSame(ClusterService service, ServiceDeployment deployment, ServiceFiles files) {
        cli.execute("apply -f " + files.getPath("resources"))
                .throwExceptionIfError();
        deployHook.call(service, deployment, files);
    }
}

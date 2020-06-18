package io.osdf.management.deploy.deployers;

import io.cluster.old.cluster.cli.ClusterCli;
import io.osdf.management.deploy.deployers.hooks.DeployHook;
import io.microconfig.osdf.service.ClusterService;
import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.files.ServiceFiles;
import lombok.RequiredArgsConstructor;

import static io.cluster.old.cluster.resource.tools.ResourceCleaner.resourceCleaner;
import static io.osdf.management.deploy.deployers.hooks.EmptyHook.emptyHook;
import static io.microconfig.utils.Logger.info;

@RequiredArgsConstructor
public class BaseServiceDeployer implements ServiceDeployer {
    private final ClusterCli cli;
    private final DeployHook deployHook;

    public static BaseServiceDeployer baseServiceDeployer(ClusterCli cli) {
        return new BaseServiceDeployer(cli, emptyHook());
    }

    public static BaseServiceDeployer baseServiceDeployer(ClusterCli cli, DeployHook hook) {
        return new BaseServiceDeployer(cli, hook);
    }

    @Override
    public void deploy(ClusterService service, ServiceDeployment deployment, ServiceFiles files) {
        info("Deploying " + service.name());

        resourceCleaner(cli).cleanOld(files.resources(), service.resources());
        deployment.createConfigMap(files.configs());
        uploadResources(service, deployment, files);
    }

    private void uploadResources(ClusterService service, ServiceDeployment deployment, ServiceFiles files) {
        cli.execute("apply -f " + files.getPath("resources"))
                .throwExceptionIfError();
        deployHook.call(service, deployment, files);
    }
}

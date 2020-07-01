package io.osdf.actions.management.deploy.deployers;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.actions.management.deploy.deployers.hooks.DeployHook;
import io.osdf.core.service.cluster.ClusterService;
import io.osdf.core.service.core.deployment.ServiceDeployment;
import io.osdf.core.service.local.ServiceFiles;
import lombok.RequiredArgsConstructor;

import static io.osdf.actions.management.deploy.cleaner.ResourceCleaner.resourceCleaner;
import static io.osdf.actions.management.deploy.deployers.hooks.EmptyHook.emptyHook;
import static io.microconfig.utils.Logger.announce;
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
        announce("Deploying " + service.name());

        resourceCleaner(cli).cleanOld(files.resources(), service.resources());
        deployment.createConfigMap(files.configs());
        uploadResources(service, deployment, files);
    }

    private void uploadResources(ClusterService service, ServiceDeployment deployment, ServiceFiles files) {
        String output = cli.execute("apply -f " + files.getPath("resources")).getOutput();
        if (output.contains("field is immutable")) {
            info("One of resources changed immutable field");
            service.upload(files.resources());
        }
        deployHook.call(service, deployment, files);
    }
}

package io.microconfig.osdf.deployers;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.deployers.hooks.DeployHook;
import io.microconfig.osdf.service.ClusterService;
import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.files.ServiceFiles;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.cluster.resource.tools.ResourceCleaner.resourceCleaner;
import static io.microconfig.osdf.deployers.hooks.EmptyHook.emptyHook;
import static io.microconfig.utils.Logger.announce;
import static io.microconfig.utils.Logger.info;

@RequiredArgsConstructor
public class BaseServiceDeployer implements ServiceDeployer {
    private final ClusterCLI cli;
    private final DeployHook deployHook;

    public static BaseServiceDeployer baseServiceDeployer(ClusterCLI cli) {
        return new BaseServiceDeployer(cli, emptyHook());
    }

    public static BaseServiceDeployer baseServiceDeployer(ClusterCLI cli, DeployHook hook) {
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

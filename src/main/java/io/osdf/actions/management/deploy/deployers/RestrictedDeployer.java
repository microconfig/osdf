package io.osdf.actions.management.deploy.deployers;

import io.osdf.core.service.cluster.ClusterService;
import io.osdf.core.service.core.deployment.ServiceDeployment;
import io.osdf.core.service.local.ServiceFiles;

import static io.microconfig.utils.Logger.info;

public class RestrictedDeployer implements ServiceDeployer {
    public static RestrictedDeployer restrictedDeployer() {
        return new RestrictedDeployer();
    }

    @Override
    public void deploy(ClusterService service, ServiceDeployment deployment, ServiceFiles files) {
        info("Deploying " + service.name());
        deployment.createConfigMap(files.configs());
        service.upload(files.resources());
    }
}

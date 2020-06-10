package io.microconfig.osdf.deployers;

import io.microconfig.osdf.service.ClusterService;
import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.files.ServiceFiles;

import static io.microconfig.utils.Logger.info;

public class RestrictedDeployer implements ServiceDeployer {
    public static RestrictedDeployer restrictedDeployer() {
        return new RestrictedDeployer();
    }

    @Override
    public void deploy(ClusterService service, ServiceDeployment deployment, ServiceFiles files) {
        info("Deploying " + service.name());
        deployment.createConfigMap(files);
        service.upload(files.resources());
    }
}

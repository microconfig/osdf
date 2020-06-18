package io.osdf.management.deploy.deployers.hooks;

import io.microconfig.osdf.service.ClusterService;
import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.files.ServiceFiles;

public interface DeployHook {
    void call(ClusterService service, ServiceDeployment deployment, ServiceFiles files);
}

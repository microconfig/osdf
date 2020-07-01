package io.osdf.actions.management.deploy.deployers.hooks;

import io.osdf.core.service.cluster.ClusterService;
import io.osdf.core.service.core.deployment.ServiceDeployment;
import io.osdf.core.service.local.ServiceFiles;

public interface DeployHook {
    void call(ClusterService service, ServiceDeployment deployment, ServiceFiles files);
}

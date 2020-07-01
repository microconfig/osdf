package io.osdf.actions.management.deploy.deployers;

import io.osdf.core.service.cluster.ClusterService;
import io.osdf.core.service.core.deployment.ServiceDeployment;
import io.osdf.core.service.local.ServiceFiles;

public interface ServiceDeployer {
    void deploy(ClusterService service, ServiceDeployment deployment, ServiceFiles files);
}

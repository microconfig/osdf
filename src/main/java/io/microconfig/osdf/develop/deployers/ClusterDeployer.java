package io.microconfig.osdf.develop.deployers;

import io.microconfig.osdf.develop.service.ClusterService;
import io.microconfig.osdf.develop.deployment.ServiceDeployment;
import io.microconfig.osdf.develop.service.ServiceFiles;

public interface ClusterDeployer {
    void deploy(ClusterService service, ServiceDeployment deployment, ServiceFiles files);
}

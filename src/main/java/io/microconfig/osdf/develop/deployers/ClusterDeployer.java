package io.microconfig.osdf.develop.deployers;

import io.microconfig.osdf.develop.service.ClusterService;
import io.microconfig.osdf.develop.deployment.ClusterDeployment;
import io.microconfig.osdf.develop.service.ServiceFiles;

public interface ClusterDeployer {
    void deploy(ClusterService service, ClusterDeployment deployment, ServiceFiles files);
}

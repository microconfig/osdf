package io.microconfig.osdf.deployers;

import io.microconfig.osdf.service.ClusterService;
import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.files.ServiceFiles;

public interface ServiceDeployer {
    void deploy(ClusterService service, ServiceDeployment deployment, ServiceFiles files);
}

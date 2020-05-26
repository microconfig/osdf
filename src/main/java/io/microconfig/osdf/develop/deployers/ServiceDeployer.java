package io.microconfig.osdf.develop.deployers;

import io.microconfig.osdf.develop.service.ClusterService;
import io.microconfig.osdf.develop.service.deployment.ServiceDeployment;
import io.microconfig.osdf.develop.service.files.ServiceFiles;

public interface ServiceDeployer {
    void deploy(ClusterService service, ServiceDeployment deployment, ServiceFiles files);
}

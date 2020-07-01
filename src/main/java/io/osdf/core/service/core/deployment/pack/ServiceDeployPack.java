package io.osdf.core.service.core.deployment.pack;

import io.osdf.core.service.cluster.ClusterService;
import io.osdf.core.service.core.deployment.ServiceDeployment;
import io.osdf.core.service.local.ServiceFiles;

public interface ServiceDeployPack {
    ServiceFiles files();

    ServiceDeployment deployment();

    ClusterService service();
}

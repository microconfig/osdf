package io.microconfig.osdf.service.deployment.pack;

import io.microconfig.osdf.service.ClusterService;
import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.files.ServiceFiles;

public interface ServiceDeployPack {
    ServiceFiles files();

    ServiceDeployment deployment();

    ClusterService service();
}

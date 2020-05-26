package io.microconfig.osdf.develop.service.deployment.pack;

import io.microconfig.osdf.develop.service.ClusterService;
import io.microconfig.osdf.develop.service.deployment.ServiceDeployment;
import io.microconfig.osdf.develop.service.files.ServiceFiles;

public interface ServiceDeployPack {
    ServiceFiles files();

    ServiceDeployment deployment();

    ClusterService service();
}

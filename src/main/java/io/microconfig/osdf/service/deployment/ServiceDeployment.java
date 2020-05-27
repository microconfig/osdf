package io.microconfig.osdf.service.deployment;

import io.microconfig.osdf.cluster.deployment.ClusterDeployment;
import io.microconfig.osdf.service.deployment.info.ServiceDeploymentInfo;
import io.microconfig.osdf.service.ServiceResource;

import java.nio.file.Path;
import java.util.List;

public interface ServiceDeployment extends ClusterDeployment, ServiceResource {
    boolean createConfigMap(List<Path> configs);

    ServiceDeploymentInfo info();
}

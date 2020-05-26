package io.microconfig.osdf.develop.service.deployment;

import io.microconfig.osdf.develop.cluster.deployment.ClusterDeployment;
import io.microconfig.osdf.develop.service.deployment.info.ServiceDeploymentInfo;
import io.microconfig.osdf.develop.service.ServiceResource;

import java.nio.file.Path;
import java.util.List;

public interface ServiceDeployment extends ClusterDeployment, ServiceResource {
    boolean createConfigMap(List<Path> configs);

    ServiceDeploymentInfo info();
}

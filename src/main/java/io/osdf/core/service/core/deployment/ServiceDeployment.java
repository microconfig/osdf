package io.osdf.core.service.core.deployment;

import io.osdf.core.cluster.deployment.ClusterDeployment;
import io.osdf.actions.info.info.deployment.ServiceDeploymentInfo;
import io.osdf.core.service.core.ServiceResource;

import java.nio.file.Path;
import java.util.List;

public interface ServiceDeployment extends ClusterDeployment, ServiceResource {
    boolean createConfigMap(List<Path> configs);

    ServiceDeploymentInfo info();
}

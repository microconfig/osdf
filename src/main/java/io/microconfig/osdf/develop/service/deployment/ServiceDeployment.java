package io.microconfig.osdf.develop.service.deployment;

import io.microconfig.osdf.develop.deployment.ClusterDeployment;
import io.microconfig.osdf.develop.deployment.info.ServiceDeploymentInfo;

import java.nio.file.Path;
import java.util.List;

public interface ServiceDeployment extends ClusterDeployment {
    String version();

    String serviceName();

    boolean createConfigMap(List<Path> configs);

    ServiceDeploymentInfo info();
}

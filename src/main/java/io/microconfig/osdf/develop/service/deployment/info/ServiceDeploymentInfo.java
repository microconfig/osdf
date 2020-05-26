package io.microconfig.osdf.develop.service.deployment.info;

import io.microconfig.osdf.components.info.DeploymentStatus;

public interface ServiceDeploymentInfo {
    int replicas();

    int availableReplicas();

    String version();

    String configVersion();

    String hash();

    DeploymentStatus status();
}
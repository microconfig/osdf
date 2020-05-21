package io.microconfig.osdf.develop.deployment.info;

import io.microconfig.osdf.components.info.DeploymentStatus;

public interface ClusterDeploymentInfo {
    int replicas();

    int availableReplicas();

    String version();

    String configVersion();

    String hash();

    DeploymentStatus status();
}
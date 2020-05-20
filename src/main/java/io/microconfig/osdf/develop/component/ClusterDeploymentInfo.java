package io.microconfig.osdf.develop.component;

import io.microconfig.osdf.components.info.DeploymentStatus;

public interface ClusterDeploymentInfo {
    int replicas();

    int availableReplicas();

    String projectVersion();

    String configVersion();

    String hash();

    DeploymentStatus status();
}
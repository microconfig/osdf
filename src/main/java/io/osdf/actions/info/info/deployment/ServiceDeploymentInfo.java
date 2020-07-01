package io.osdf.actions.info.info.deployment;

public interface ServiceDeploymentInfo {
    int replicas();

    int availableReplicas();

    String version();

    String configVersion();

    String hash();

    DeploymentStatus status();
}
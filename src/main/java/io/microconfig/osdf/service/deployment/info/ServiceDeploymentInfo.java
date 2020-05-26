package io.microconfig.osdf.service.deployment.info;

public interface ServiceDeploymentInfo {
    int replicas();

    int availableReplicas();

    String version();

    String configVersion();

    String hash();

    DeploymentStatus status();
}
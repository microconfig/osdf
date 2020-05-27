package io.microconfig.osdf.service.deployment.info;

public enum DeploymentStatus {
    RUNNING,
    BAD_HEALTHCHECK,
    NOT_READY,
    TURNED_OFF,
    NOT_FOUND,
    FAILED
}

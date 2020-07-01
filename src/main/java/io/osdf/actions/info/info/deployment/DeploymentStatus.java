package io.osdf.actions.info.info.deployment;

public enum DeploymentStatus {
    RUNNING,
    BAD_HEALTHCHECK,
    NOT_READY,
    TURNED_OFF,
    NOT_FOUND,
    FAILED
}

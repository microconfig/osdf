package io.osdf.actions.info.healthcheck.healthchecker;

import io.osdf.core.cluster.pod.Pod;

public interface HealthChecker {
    boolean check(Pod pod);
}

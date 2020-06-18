package io.microconfig.osdf.service.deployment.checkers.healthcheck;

import io.cluster.old.cluster.pod.Pod;

public interface HealthChecker {
    boolean check(Pod pod);
}

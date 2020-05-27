package io.microconfig.osdf.service.deployment.checkers.healthcheck;

import io.microconfig.osdf.cluster.pod.Pod;

public interface HealthChecker {
    boolean check(Pod pod);
}

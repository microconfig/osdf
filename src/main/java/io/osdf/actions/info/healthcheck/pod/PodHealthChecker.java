package io.osdf.actions.info.healthcheck.pod;

import io.osdf.core.cluster.pod.Pod;

public interface PodHealthChecker {
    boolean check(Pod pod);
}

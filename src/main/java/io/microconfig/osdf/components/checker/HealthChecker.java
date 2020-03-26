package io.microconfig.osdf.components.checker;

import io.microconfig.osdf.openshift.Pod;

public interface HealthChecker {
    boolean check(Pod pod);
}

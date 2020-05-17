package io.microconfig.osdf.components.checker;

import io.microconfig.osdf.components.DeploymentComponent;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.components.checker.LogHealthChecker.logHealthChecker;

@RequiredArgsConstructor
public class HealthCheckerFinder {
    private final DeploymentComponent component;

    public static HealthCheckerFinder healthCheckerFinder(DeploymentComponent component) {
        return new HealthCheckerFinder(component);
    }

    public HealthChecker get() {
        return logHealthChecker(component);
    }
}

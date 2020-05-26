package io.microconfig.osdf.service.deployment.checkers.healthcheck;

import io.microconfig.osdf.service.files.ServiceFiles;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.service.deployment.checkers.healthcheck.LogHealthChecker.logHealthChecker;

@RequiredArgsConstructor
public class HealthCheckerFinder {
    private final ServiceFiles files;

    public static HealthCheckerFinder healthCheckerFinder(ServiceFiles files) {
        return new HealthCheckerFinder(files);
    }

    public HealthChecker get() {
        return logHealthChecker(files);
    }
}

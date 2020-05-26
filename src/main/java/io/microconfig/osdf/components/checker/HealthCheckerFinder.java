package io.microconfig.osdf.components.checker;

import io.microconfig.osdf.develop.service.files.ServiceFiles;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.components.checker.LogHealthChecker.logHealthChecker;

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

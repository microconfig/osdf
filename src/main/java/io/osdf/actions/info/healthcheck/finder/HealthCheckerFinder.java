package io.osdf.actions.info.healthcheck.finder;

import io.osdf.actions.info.healthcheck.healthchecker.HealthChecker;
import io.osdf.core.service.local.ServiceFiles;
import lombok.RequiredArgsConstructor;

import static io.osdf.actions.info.healthcheck.healthchecker.ReadinessHealthChecker.readinessHealthChecker;
import static io.osdf.common.utils.YamlUtils.get;
import static io.osdf.common.utils.YamlUtils.loadFromPath;
import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
public class HealthCheckerFinder {
    private final ServiceFiles files;
    private final int timeout;

    public static HealthCheckerFinder healthCheckerFinder(ServiceFiles files, int timeout) {
        return new HealthCheckerFinder(files, timeout);
    }

    public HealthChecker find() {
        Object deployFile = loadFromPath(files.getPath("deploy.yaml"));
        return readinessHealthChecker(timeout(deployFile));
    }

    private int timeout(Object deployFile) {
        if (timeout > 0) return timeout;
        Integer timeoutInSec = get(deployFile, "osdf.start.waitSec");
        return ofNullable(timeoutInSec).orElse(60);
    }
}

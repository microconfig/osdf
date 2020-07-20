package io.osdf.actions.info.healthcheck;

import io.osdf.actions.info.healthcheck.pod.PodHealthChecker;
import io.osdf.common.yaml.YamlObject;
import io.osdf.core.application.core.files.ApplicationFiles;
import lombok.RequiredArgsConstructor;

import static io.osdf.actions.info.healthcheck.pod.ReadinessPodHealthChecker.readinessHealthChecker;
import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
public class HealthCheckerFinder {
    private final ApplicationFiles files;
    private final int timeout;

    public static HealthCheckerFinder healthCheckerFinder(ApplicationFiles files, int timeout) {
        return new HealthCheckerFinder(files, timeout);
    }

    public PodHealthChecker find() {
        return readinessHealthChecker(timeout(files.deployProperties()));
    }

    private int timeout(YamlObject deployProperties) {
        if (timeout > 0) return timeout;
        Integer timeoutInSec = deployProperties.get("osdf.start.waitSec");
        return ofNullable(timeoutInSec).orElse(60);
    }
}

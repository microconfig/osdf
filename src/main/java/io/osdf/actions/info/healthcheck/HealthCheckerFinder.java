package io.osdf.actions.info.healthcheck;

import io.osdf.actions.info.healthcheck.pod.PodHealthChecker;
import io.osdf.core.application.core.files.ApplicationFiles;
import lombok.RequiredArgsConstructor;

import static io.osdf.actions.info.healthcheck.pod.ReadinessPodHealthChecker.readinessHealthChecker;
import static io.osdf.common.utils.YamlUtils.get;
import static io.osdf.common.utils.YamlUtils.loadFromPath;
import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
public class HealthCheckerFinder {
    private final ApplicationFiles files;
    private final int timeout;

    public static HealthCheckerFinder healthCheckerFinder(ApplicationFiles files, int timeout) {
        return new HealthCheckerFinder(files, timeout);
    }

    public PodHealthChecker find() {
        Object deployFile = loadFromPath(files.getPath("deploy.yaml"));
        return readinessHealthChecker(timeout(deployFile));
    }

    private int timeout(Object deployFile) {
        if (timeout > 0) return timeout;
        Integer timeoutInSec = get(deployFile, "osdf.start.waitSec");
        return ofNullable(timeoutInSec).orElse(60);
    }
}

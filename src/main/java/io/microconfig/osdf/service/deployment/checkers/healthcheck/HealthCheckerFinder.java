package io.microconfig.osdf.service.deployment.checkers.healthcheck;

import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.service.files.ServiceFiles;
import io.microconfig.osdf.utils.YamlUtils;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static io.microconfig.osdf.utils.YamlUtils.getInt;
import static io.microconfig.osdf.utils.YamlUtils.loadFromPath;
import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
public class HealthCheckerFinder {
    private final ServiceFiles files;
    private final int timeout;

    public static HealthCheckerFinder healthCheckerFinder(ServiceFiles files, int timeout) {
        return new HealthCheckerFinder(files, timeout);
    }

    public HealthChecker get() {
        return logHealthChecker();
    }

    private HealthChecker logHealthChecker() {
        String marker = YamlUtils.get(loadFromPath(files.getPath("deploy.yaml")), "healthcheck.marker.success");
        if (marker == null) throw new OSDFException("Marker not found for log healthchecker");
        return LogHealthChecker.logHealthChecker(marker, timeout());
    }

    private int timeout() {
        if (timeout > 0) return timeout;
        Map<String, Object> deployProperties = loadFromPath(files.getPath("deploy.yaml"));
        Integer timeoutInSec = getInt(deployProperties, "osdf", "start", "waitSec");
        return ofNullable(timeoutInSec).orElse(60);
    }
}

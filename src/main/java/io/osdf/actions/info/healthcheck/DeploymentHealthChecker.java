package io.osdf.actions.info.healthcheck;

import io.osdf.core.service.core.deployment.ServiceDeployment;
import io.osdf.core.service.local.ServiceFiles;
import lombok.RequiredArgsConstructor;

import static io.osdf.actions.info.info.deployment.DeploymentStatus.READY;
import static io.osdf.common.utils.ThreadUtils.sleepSec;
import static io.osdf.common.utils.YamlUtils.get;
import static io.osdf.common.utils.YamlUtils.loadFromPath;

@RequiredArgsConstructor
public class DeploymentHealthChecker {
    private final int timeout;

    public static DeploymentHealthChecker deploymentHealthChecker() {
        return new DeploymentHealthChecker(0);
    }

    public static DeploymentHealthChecker deploymentHealthChecker(int timeout) {
        return new DeploymentHealthChecker(timeout);
    }

    public boolean check(ServiceDeployment deployment, ServiceFiles files) {
        Integer podStartTime = podStartTime(files);
        int currentTime = 0;
        while (deployment.info().status() != READY) {
            currentTime++;
            if (currentTime > podStartTime) return false;
            sleepSec(1);
        }
        return true;
    }

    private Integer podStartTime(ServiceFiles files) {
        if (timeout > 0) return timeout;
        Integer configuredTimeout = get(loadFromPath(files.getPath("deploy.yaml")), "osdf.start.podWaitSec");
        return configuredTimeout == null ? 60 : configuredTimeout;
    }
}

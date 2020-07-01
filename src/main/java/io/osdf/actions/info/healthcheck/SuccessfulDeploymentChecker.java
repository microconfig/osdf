package io.osdf.actions.info.healthcheck;

import io.osdf.core.service.core.deployment.ServiceDeployment;
import io.osdf.actions.info.info.deployment.DeploymentStatus;
import io.osdf.core.service.local.ServiceFiles;
import lombok.RequiredArgsConstructor;

import static io.osdf.actions.info.healthcheck.finder.HealthCheckerFromFiles.podsInfo;
import static io.osdf.actions.info.info.deployment.DeploymentStatus.RUNNING;
import static io.osdf.common.utils.ThreadUtils.sleepSec;
import static io.osdf.common.utils.YamlUtils.get;
import static io.osdf.common.utils.YamlUtils.loadFromPath;
import static java.lang.Math.max;
import static java.lang.System.currentTimeMillis;

@RequiredArgsConstructor
public class SuccessfulDeploymentChecker {
    private final int timeout;

    public static SuccessfulDeploymentChecker successfulDeploymentChecker() {
        return new SuccessfulDeploymentChecker(0);
    }

    public static SuccessfulDeploymentChecker successfulDeploymentChecker(int timeout) {
        return new SuccessfulDeploymentChecker(timeout);
    }

    public boolean check(ServiceDeployment deployment, ServiceFiles files) {
        long startTime = currentTimeMillis();
        Integer podStartTime = podStartTime(files);
        int currentTime = 0;
        while (deployment.info().status() != RUNNING) {
            currentTime++;
            if (currentTime > podStartTime) return false;
            sleepSec(1);
        }
        int timeLeft = max(1, podStartTime - calcSecFrom(startTime));
        DeploymentStatus status = deployment.info().status();
        return status == RUNNING && podsInfo(deployment, files, timeLeft).isHealthy();
    }

    private Integer podStartTime(ServiceFiles files) {
        if (timeout > 0) return timeout;
        Integer configuredTimeout = get(loadFromPath(files.getPath("deploy.yaml")), "osdf.start.podWaitSec");
        return configuredTimeout == null ? 60 : configuredTimeout;
    }

    private int calcSecFrom(long startTime) {
        return (int) ((currentTimeMillis() - startTime) / 1000);
    }
}

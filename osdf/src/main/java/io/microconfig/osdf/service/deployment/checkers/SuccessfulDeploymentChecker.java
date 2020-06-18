package io.microconfig.osdf.service.deployment.checkers;

import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.deployment.info.DeploymentStatus;
import io.microconfig.osdf.service.files.ServiceFiles;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.service.deployment.info.DeploymentStatus.RUNNING;
import static io.microconfig.osdf.healthcheck.HealthcheckerFromFiles.podsInfo;
import static io.microconfig.osdf.deprecated.components.properties.DeployProperties.deployProperties;
import static io.microconfig.osdf.utils.ThreadUtils.sleepSec;
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
        return deployProperties(files.root()).getPodStartTime();
    }

    private int calcSecFrom(long startTime) {
        return (int) ((currentTimeMillis() - startTime) / 1000);
    }
}

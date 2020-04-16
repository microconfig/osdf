package io.microconfig.osdf.components.checker;

import io.microconfig.osdf.components.DeploymentComponent;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.components.info.DeploymentStatus.RUNNING;
import static io.microconfig.osdf.components.info.PodsHealthcheckInfo.podsInfo;
import static io.microconfig.osdf.utils.ThreadUtils.sleepSec;

@RequiredArgsConstructor
public class SuccessfulDeploymentChecker {
    private final HealthChecker healthChecker;

    public static SuccessfulDeploymentChecker successfulDeploymentChecker(HealthChecker healthChecker) {
        return new SuccessfulDeploymentChecker(healthChecker);
    }

    public boolean check(DeploymentComponent component) {
        Integer podStartTime = component.deployProperties().getPodStartTime();
        int currentTime = 0;
        while (!component.isRunning()) {
            currentTime++;
            if (currentTime > podStartTime) return false;
            sleepSec(1);
        }
        return component.info().getStatus() == RUNNING && podsInfo(component, healthChecker).isHealthy();
    }
}

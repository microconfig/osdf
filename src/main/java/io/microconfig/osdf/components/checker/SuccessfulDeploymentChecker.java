package io.microconfig.osdf.components.checker;

import io.microconfig.osdf.components.DeploymentComponent;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.components.info.DeploymentStatus.RUNNING;
import static io.microconfig.osdf.components.info.PodsHealthcheckInfo.podsInfo;
import static io.microconfig.osdf.components.properties.DeployProperties.deployProperties;
import static io.microconfig.osdf.utils.ThreadUtils.sleepSec;

@RequiredArgsConstructor
public class SuccessfulDeploymentChecker {
    public static SuccessfulDeploymentChecker successfulDeploymentChecker() {
        return new SuccessfulDeploymentChecker();
    }

    public boolean check(DeploymentComponent component) {
        Integer podStartTime = deployProperties(component.getConfigDir()).getPodStartTime();
        int currentTime = 0;
        while (!component.isRunning()) {
            currentTime++;
            if (currentTime > podStartTime) return false;
            sleepSec(1);
        }
        return component.info().getStatus() == RUNNING && podsInfo(component).isHealthy();
    }
}

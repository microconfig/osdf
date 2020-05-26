package io.microconfig.osdf.components.checker;

import io.microconfig.osdf.develop.service.deployment.ServiceDeployment;
import io.microconfig.osdf.develop.service.files.ServiceFiles;
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

    public boolean check(ServiceDeployment deployment, ServiceFiles files) {
        Integer podStartTime = deployProperties(files.root()).getPodStartTime();
        int currentTime = 0;
        while (deployment.info().status() != RUNNING) {
            currentTime++;
            if (currentTime > podStartTime) return false;
            sleepSec(1);
        }
        return deployment.info().status() == RUNNING && podsInfo(deployment, files).isHealthy();
    }
}

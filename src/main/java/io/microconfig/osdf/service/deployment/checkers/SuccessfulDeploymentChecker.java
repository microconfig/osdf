package io.microconfig.osdf.service.deployment.checkers;

import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.files.ServiceFiles;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.service.deployment.info.DeploymentStatus.RUNNING;
import static io.microconfig.osdf.service.deployment.info.PodsHealthcheckInfo.podsInfo;
import static io.microconfig.osdf.deprecated.components.properties.DeployProperties.deployProperties;
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

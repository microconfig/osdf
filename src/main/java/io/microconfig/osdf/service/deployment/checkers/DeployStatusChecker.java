package io.microconfig.osdf.service.deployment.checkers;

import io.microconfig.osdf.service.deployment.pack.ServiceDeployPack;

import java.util.List;

import static io.microconfig.osdf.service.deployment.checkers.SuccessfulDeploymentChecker.successfulDeploymentChecker;

public class DeployStatusChecker {
    public static DeployStatusChecker deployStatusChecker() {
        return new DeployStatusChecker();
    }

    public boolean check(List<ServiceDeployPack> deployPacks) {
        SuccessfulDeploymentChecker checker = successfulDeploymentChecker();
        return deployPacks.parallelStream().allMatch(deployPack -> checkDeployment(checker, deployPack));
    }

    private boolean checkDeployment(SuccessfulDeploymentChecker checker, ServiceDeployPack deployPack) {
        return checker.check(deployPack.deployment(), deployPack.files());
    }
}

package io.microconfig.osdf.service.deployment.checkers;

import io.microconfig.osdf.service.deployment.pack.ServiceDeployPack;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.service.deployment.checkers.SuccessfulDeploymentChecker.successfulDeploymentChecker;
import static io.microconfig.utils.ConsoleColor.green;
import static io.microconfig.utils.ConsoleColor.red;
import static io.microconfig.utils.Logger.info;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.IntStream.range;

@RequiredArgsConstructor
public class DeployStatusChecker {
    private final int timeout;

    public static DeployStatusChecker deployStatusChecker(int timeout) {
        return new DeployStatusChecker(timeout);
    }

    public static DeployStatusChecker deployStatusChecker() {
        return new DeployStatusChecker(0);
    }

    public List<ServiceDeployPack> findFailed(List<ServiceDeployPack> deployPacks) {
        SuccessfulDeploymentChecker checker = successfulDeploymentChecker(timeout);
        List<Boolean> results = deployPacks
                .parallelStream()
                .map(deployPack -> checkDeployment(checker, deployPack))
                .collect(toUnmodifiableList());
        return range(0, deployPacks.size())
                .filter(i -> !results.get(i))
                .mapToObj(deployPacks::get)
                .collect(toUnmodifiableList());
    }

    private boolean checkDeployment(SuccessfulDeploymentChecker checker, ServiceDeployPack deployPack) {
        boolean check = checker.check(deployPack.deployment(), deployPack.files());
        info(deployPack.service().name() + " " + (check ? green("OK") : red("FAILED")));
        return check;
    }
}

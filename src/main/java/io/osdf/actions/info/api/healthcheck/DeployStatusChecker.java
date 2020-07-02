package io.osdf.actions.info.api.healthcheck;

import io.osdf.actions.info.healthcheck.DeploymentHealthChecker;
import io.osdf.core.service.core.deployment.pack.ServiceDeployPack;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.utils.ConsoleColor.green;
import static io.microconfig.utils.ConsoleColor.red;
import static io.microconfig.utils.Logger.info;
import static io.osdf.actions.info.healthcheck.DeploymentHealthChecker.deploymentHealthChecker;
import static io.osdf.common.utils.ThreadUtils.runInParallel;
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
        DeploymentHealthChecker checker = deploymentHealthChecker(timeout);

        List<Boolean> results = getResults(deployPacks, checker);
        return range(0, deployPacks.size())
                .filter(i -> !results.get(i))
                .mapToObj(deployPacks::get)
                .collect(toUnmodifiableList());
    }

    private List<Boolean> getResults(List<ServiceDeployPack> deployPacks, DeploymentHealthChecker checker) {
        return runInParallel(deployPacks.size(),
                () -> deployPacks
                        .parallelStream()
                        .map(deployPack -> checkDeployment(checker, deployPack))
                        .collect(toUnmodifiableList()));
    }

    private boolean checkDeployment(DeploymentHealthChecker checker, ServiceDeployPack deployPack) {
        boolean check = checker.check(deployPack.deployment(), deployPack.files());
        info(deployPack.service().name() + " " + (check ? green("OK") : red("FAILED")));
        return check;
    }
}

package io.osdf.actions.info.api.healthcheck;

import io.osdf.actions.info.healthcheck.DeploymentHealthChecker;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.connection.cli.ClusterCli;
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
    private final ClusterCli cli;

    public static DeployStatusChecker deployStatusChecker(int timeout, ClusterCli cli) {
        return new DeployStatusChecker(timeout, cli);
    }

    public static DeployStatusChecker deployStatusChecker(ClusterCli cli) {
        return new DeployStatusChecker(0, cli);
    }

    public List<ServiceApplication> findFailed(List<ServiceApplication> services) {
        DeploymentHealthChecker checker = deploymentHealthChecker(timeout, cli);

        List<Boolean> results = getResults(services, checker);
        return range(0, services.size())
                .filter(i -> !results.get(i))
                .mapToObj(services::get)
                .collect(toUnmodifiableList());
    }

    private List<Boolean> getResults(List<ServiceApplication> services, DeploymentHealthChecker checker) {
        return runInParallel(services.size(),
                () -> services
                        .parallelStream()
                        .map(deployPack -> checkDeployment(checker, deployPack))
                        .collect(toUnmodifiableList()));
    }

    private boolean checkDeployment(DeploymentHealthChecker checker, ServiceApplication service) {
        boolean check = checker.check(service);
        info(service.files().name() + " " + (check ? green("OK") : red("FAILED")));
        return check;
    }
}

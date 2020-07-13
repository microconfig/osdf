package io.osdf.actions.info.api.healthcheck;

import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.cluster.resource.ClusterResource;
import io.osdf.core.connection.cli.CliOutput;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.utils.ConsoleColor.green;
import static io.microconfig.utils.ConsoleColor.red;
import static io.microconfig.utils.Logger.error;
import static io.microconfig.utils.Logger.info;
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

    public List<ServiceApplication> findFailed(List<ServiceApplication> services) {
        List<Boolean> results = getResults(services);
        return range(0, services.size())
                .filter(i -> !results.get(i))
                .mapToObj(services::get)
                .collect(toUnmodifiableList());
    }

    private List<Boolean> getResults(List<ServiceApplication> services) {
        return runInParallel(services.size(),
                () -> services
                        .parallelStream()
                        .map(this::checkDeployment)
                        .collect(toUnmodifiableList()));
    }

    private boolean checkDeployment(ServiceApplication service) {
        if (!service.exists()) {
            return withLogging(service.name(), false);
        }

        ClusterResource deployment = service.deployment().toResource();
        CliOutput result = cli.execute("rollout status " + deployment.kind() + "/" + deployment.name(), timeout);
        if (!result.ok()) {
            List<String> outputLines = result.getOutputLines();
            error(service.name() + ": " + outputLines.get(outputLines.size() - 1));
        }
        return withLogging(service.name(), result.ok());
    }

    private boolean withLogging(String name, boolean status) {
        info(name + " " + (status ? green("OK") : red("FAILED")));
        return status;
    }
}

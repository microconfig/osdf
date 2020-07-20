package io.osdf.actions.info.healthcheck.app;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.core.Application;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.cluster.resource.ClusterResource;
import io.osdf.core.connection.cli.CliOutput;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.utils.Logger.error;
import static java.util.Objects.requireNonNullElse;

@RequiredArgsConstructor
public class ServiceHealthChecker implements AppHealthChecker{
    private final ClusterCli cli;

    public static ServiceHealthChecker serviceHealthChecker(ClusterCli cli) {
        return new ServiceHealthChecker(cli);
    }

    @Override
    public boolean check(Application app) {
        if (!(app instanceof ServiceApplication)) throw new OSDFException(app.name() + " is not a service");
        ServiceApplication service = (ServiceApplication) app;

        if (!service.exists()) return false;

        ClusterResource deployment = service.deployment().toResource();
        CliOutput result = cli.execute("rollout status " + deployment.kind() + "/" + deployment.name(),
                timeout(service));
        if (!result.ok()) {
            List<String> outputLines = result.getOutputLines();
            error(service.name() + ": " + outputLines.get(outputLines.size() - 1));
        }
        return result.ok();
    }

    private int timeout(ServiceApplication service) {
        Integer timeoutInSec = service.files().deployProperties()
                .get("osdf.healthcheck.timeoutInSec");
        return requireNonNullElse(timeoutInSec, 60);
    }
}

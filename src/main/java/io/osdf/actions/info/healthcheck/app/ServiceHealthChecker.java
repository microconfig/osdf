package io.osdf.actions.info.healthcheck.app;

import io.osdf.core.application.core.Application;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.cluster.deployment.ClusterDeployment;
import io.osdf.core.cluster.resource.ClusterResource;
import io.osdf.core.connection.cli.CliOutput;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Optional;

import static io.microconfig.utils.Logger.error;
import static io.osdf.common.utils.StringUtils.castToInteger;
import static io.osdf.core.application.service.ServiceApplication.serviceApplication;
import static java.lang.System.getenv;
import static java.util.Objects.requireNonNullElse;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class ServiceHealthChecker implements AppHealthChecker {
    private final ClusterCli cli;
    @Setter
    private int customTimeout = 0;

    public static ServiceHealthChecker serviceHealthChecker(ClusterCli cli) {
        return new ServiceHealthChecker(cli);
    }

    @Override
    public boolean check(Application app) {
        ServiceApplication service = serviceApplication(app);

        Optional<ClusterDeployment> deployment = service.deployment();
        if (deployment.isEmpty()) return false;

        ClusterResource deploymentResource = deployment.get().toResource();
        CliOutput result = cli.execute("rollout status " + deploymentResource.kind() + "/" + deploymentResource.name(),
                timeout(service));
        if (!result.ok()) {
            List<String> outputLines = result.getOutputLines();
            error(service.name() + ": " + outputLines.get(outputLines.size() - 1));
        }
        return result.ok();
    }

    private int timeout(ServiceApplication service) {
        if (customTimeout != 0) return customTimeout;

        Integer timeoutFromEnv = timeoutFromEnv();
        if (timeoutFromEnv != null) return timeoutFromEnv;

        Integer timeoutInSec = service.files().deployProperties()
                .get("osdf.healthcheck.timeoutInSec");
        return requireNonNullElse(timeoutInSec, 60);
    }

    private Integer timeoutFromEnv() {
        String timeoutFromEnv = getenv("OSDF_HEALTHCHECK_TIMEOUT");
        if (timeoutFromEnv != null) return castToInteger(timeoutFromEnv.trim());
        return null;
    }
}

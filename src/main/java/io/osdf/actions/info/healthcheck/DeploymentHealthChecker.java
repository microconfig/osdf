package io.osdf.actions.info.healthcheck;

import io.osdf.actions.info.status.service.ServiceStatusGetter;
import io.osdf.core.application.local.ApplicationFiles;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import static io.osdf.actions.info.status.service.ServiceStatus.READY;
import static io.osdf.actions.info.status.service.ServiceStatusGetter.serviceStatusGetter;
import static io.osdf.common.utils.ThreadUtils.sleepSec;
import static io.osdf.common.utils.YamlUtils.get;
import static io.osdf.common.utils.YamlUtils.loadFromPath;

@RequiredArgsConstructor
public class DeploymentHealthChecker {
    private final int timeout;
    private final ClusterCli cli;

    public static DeploymentHealthChecker deploymentHealthChecker(ClusterCli cli) {
        return new DeploymentHealthChecker(0, cli);
    }

    public static DeploymentHealthChecker deploymentHealthChecker(int timeout, ClusterCli cli) {
        return new DeploymentHealthChecker(timeout, cli);
    }

    public boolean check(ServiceApplication service) {
        ServiceStatusGetter statusGetter = serviceStatusGetter(cli);
        Integer podStartTime = podStartTime(service.files());

        int currentTime = 0;
        while (statusGetter.statusOf(service) != READY) {
            currentTime++;
            if (currentTime > podStartTime) return false;
            sleepSec(1);
        }
        return true;
    }

    private Integer podStartTime(ApplicationFiles files) {
        if (timeout > 0) return timeout;
        Integer configuredTimeout = get(loadFromPath(files.getPath("deploy.yaml")), "osdf.start.podWaitSec");
        return configuredTimeout == null ? 60 : configuredTimeout;
    }
}

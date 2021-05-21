package io.osdf.actions.info.api.healthcheck;

import io.osdf.core.application.core.Application;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.utils.ConsoleColor.green;
import static io.microconfig.utils.ConsoleColor.red;
import static io.microconfig.utils.Logger.info;
import static io.osdf.actions.info.healthcheck.app.AppHealthChecker.of;
import static io.osdf.actions.management.deploy.deployer.AppHealth.OK;
import static io.osdf.common.utils.ThreadUtils.runInParallel;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class AppsStatusChecker {
    private final ClusterCli cli;

    public static AppsStatusChecker deployStatusChecker(ClusterCli cli) {
        return new AppsStatusChecker(cli);
    }

    public List<Application> findFailed(List<? extends Application> apps) {
        return runInParallel(apps.size(),
                () -> apps
                        .parallelStream()
                        .filter(not(this::isReady))
                        .collect(toUnmodifiableList())
        );
    }

    private boolean isReady(Application app) {
        boolean ok = of(app, cli).check(app) == OK;
        info(app.name() + " " + (ok ? green("OK") : red("FAILED")));
        return ok;
    }
}

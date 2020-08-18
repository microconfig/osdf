package io.osdf.actions.management.deploy;

import io.osdf.actions.management.deploy.deployer.Deployable;
import io.osdf.core.application.core.Application;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Stream;

import static io.microconfig.utils.ConsoleColor.green;
import static io.microconfig.utils.ConsoleColor.red;
import static io.microconfig.utils.Logger.announce;
import static io.microconfig.utils.Logger.info;
import static io.osdf.actions.management.deploy.deployer.Deployable.of;
import static io.osdf.common.utils.ThreadUtils.runInParallel;
import static java.lang.System.getenv;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class GroupDeployer {
    private final ClusterCli cli;
    private final Integer maxParallel;
    private final boolean failFast;

    public static GroupDeployer groupDeployer(ClusterCli cli, Integer maxParallel) {
        return new GroupDeployer(cli, maxParallel, !"false".equals(getenv("OSDF_DEPLOY_FAILFAST")));
    }

    public boolean deployGroup(List<Application> apps) {
        announce("\nDeploying group - " + apps.stream().map(Application::name).collect(toUnmodifiableList()));
        return runInParallel(parallelism(apps), () ->
                failFast ?
                        appsParallelStream(apps)
                                .allMatch(this::deployAndReturnStatus) :
                        appsParallelStream(apps)
                                .map(this::deployAndReturnStatus)
                                .collect(toUnmodifiableList()).stream()
                                .allMatch(t -> t)
        );
    }

    private Boolean deployAndReturnStatus(Deployable app) {
        if (!app.deploy()) return statusWithLogging(false, app);
        return statusWithLogging(app.check(), app);
    }

    private boolean statusWithLogging(boolean status, Deployable app) {
        info(app.name() + " " + (status ? green("OK") : red("FAILED")));
        return status;
    }

    private Stream<Deployable> appsParallelStream(List<Application> apps) {
        return apps.parallelStream()
                .map(app -> of(app, cli));
    }

    private int parallelism(List<Application> apps) {
        return maxParallel == null ? apps.size() : maxParallel;
    }
}

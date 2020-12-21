package io.osdf.actions.chaos.checks;

import io.osdf.core.application.core.Application;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.actions.info.api.healthcheck.AppsStatusChecker.deployStatusChecker;
import static io.osdf.core.application.core.files.loaders.ApplicationFilesLoaderImpl.activeRequiredAppsLoader;
import static io.osdf.core.application.service.ServiceApplicationMapper.service;
import static java.util.stream.Collectors.joining;

@RequiredArgsConstructor
public class BasicChecker implements Checker {
    private final ClusterCli cli;
    private final List<ServiceApplication> apps;

    public static BasicChecker basicChecker(ClusterCli cli, OsdfPaths paths) {
        List<ServiceApplication> apps = activeRequiredAppsLoader(paths, null).load(service(cli));
        return new BasicChecker(cli, apps);
    }

    @Override
    public CheckerResponse check() {
        List<Application> failed = deployStatusChecker(cli)
                .logReadiness(false)
                .findFailed(apps);
        if (failed.isEmpty()) return new CheckerResponse(true, "All apps are healthy");

        return new CheckerResponse(false, "Failed apps - " + appsToString(failed));
    }

    private String appsToString(List<Application> failed) {
        return failed.stream().map(Application::name).collect(joining(" "));
    }
}

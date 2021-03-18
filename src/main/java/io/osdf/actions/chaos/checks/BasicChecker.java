package io.osdf.actions.chaos.checks;

import io.osdf.actions.chaos.utils.TimeUtils;
import io.osdf.actions.info.healthcheck.app.ServiceHealthChecker;
import io.osdf.core.application.core.Application;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static io.osdf.actions.chaos.utils.TimeUtils.durationFromString;
import static io.osdf.actions.info.healthcheck.app.ServiceHealthChecker.serviceHealthChecker;
import static io.osdf.common.yaml.YamlObject.yaml;
import static io.osdf.core.application.core.files.loaders.ApplicationFilesLoaderImpl.activeRequiredAppsLoader;
import static io.osdf.core.application.service.ServiceApplicationMapper.service;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class BasicChecker implements Checker {
    private final ClusterCli cli;
    private final List<ServiceApplication> apps;
    private final int timeout;

    public static BasicChecker basicChecker(Map<String, Object> description, ClusterCli cli, OsdfPaths paths) {
        List<ServiceApplication> apps = activeRequiredAppsLoader(paths, null).load(service(cli));
        return new BasicChecker(cli, apps, durationFromString(yaml(description).getString("timeout")));
    }

    @Override
    public CheckerResponse check() {
        ServiceHealthChecker healthChecker = serviceHealthChecker(cli)
                .customTimeout(timeout);
        List<ServiceApplication> failed = apps.stream()
                .filter(not(healthChecker::check))
                .collect(toList());
        if (failed.isEmpty()) return new CheckerResponse(true, "All apps are healthy");

        return new CheckerResponse(false, "Failed apps - " + appsToString(failed));
    }

    private String appsToString(List<ServiceApplication> failed) {
        return failed.stream().map(Application::name).collect(joining(" "));
    }
}

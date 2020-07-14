package io.osdf.actions.info.api;

import io.osdf.actions.info.api.logs.LogsCommand;
import io.osdf.actions.info.api.showall.ShowAllCommand;
import io.osdf.actions.info.api.status.StatusCommand;
import io.osdf.common.exceptions.StatusCodeException;
import io.osdf.core.application.core.Application;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.actions.info.api.healthcheck.AppsStatusChecker.deployStatusChecker;
import static io.osdf.actions.info.printer.ColumnPrinter.printer;
import static io.osdf.core.application.core.AllApplications.all;
import static io.osdf.core.application.core.files.loaders.ApplicationFilesLoaderImpl.appLoader;
import static io.osdf.core.application.core.files.loaders.filters.GroupComponentsFilter.groupComponentsFilter;

@RequiredArgsConstructor
public class InfoApiImpl implements InfoApi {
    private final OsdfPaths paths;
    private final ClusterCli cli;

    public static InfoApi infoApi(OsdfPaths paths, ClusterCli cli) {
        return new InfoApiImpl(paths, cli);
    }

    @Override
    public void logs(String component, String pod) {
        cli.login();
        new LogsCommand(paths, cli).show(component, pod);
    }

    @Override
    public void status(List<String> components, Boolean withHealthCheck) {
        cli.login();
        new StatusCommand(paths, cli, printer(), withHealthCheck).run(components);
    }

    @Override
    public void healthcheck(String group, Integer timeout) {
        cli.login();
        List<Application> apps = appLoader(paths)
                .withDirFilter(groupComponentsFilter(paths, group))
                .load(all(cli));
        List<Application> failedApps = deployStatusChecker(cli)
                .findFailed(apps);
        if (!failedApps.isEmpty()) throw new StatusCodeException(1);
    }

    @Override
    public void showAll() {
        cli.login();
        new ShowAllCommand(cli, printer()).run();
    }
}

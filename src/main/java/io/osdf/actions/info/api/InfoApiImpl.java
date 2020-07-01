package io.osdf.actions.info.api;

import io.osdf.actions.info.api.logs.LogsCommand;
import io.osdf.actions.info.api.showall.ShowAllCommand;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.actions.info.api.status.StatusCommand;
import io.osdf.common.exceptions.StatusCodeException;
import io.osdf.settings.paths.OsdfPaths;
import io.osdf.core.service.core.deployment.pack.ServiceDeployPack;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.actions.info.printer.ColumnPrinter.printer;
import static io.osdf.actions.info.healthcheck.DeployStatusChecker.deployStatusChecker;
import static io.osdf.core.service.core.deployment.pack.loader.DefaultServiceDeployPacksLoader.serviceLoader;
import static io.osdf.core.service.local.loaders.filters.GroupComponentsFilter.groupComponentsFilter;

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
        List<ServiceDeployPack> deployPacks = serviceLoader(paths, groupComponentsFilter(paths, group), cli)
                .loadPacks();
        List<ServiceDeployPack> failedDeployments = deployStatusChecker(timeout == null ? 60 : timeout).findFailed(deployPacks);
        if (!failedDeployments.isEmpty()) throw new StatusCodeException(1);
    }

    @Override
    public void showAll() {
        cli.login();
        new ShowAllCommand(cli, printer()).run();
    }
}

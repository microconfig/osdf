package io.microconfig.osdf.api.implementations;

import io.microconfig.osdf.api.declarations.InfoApi;
import io.cluster.old.cluster.cli.ClusterCli;
import io.microconfig.osdf.commands.LogsCommand;
import io.microconfig.osdf.commands.ShowAllCommand;
import io.microconfig.osdf.commands.StatusCommand;
import io.microconfig.osdf.exceptions.StatusCodeException;
import io.osdf.settings.paths.OsdfPaths;
import io.microconfig.osdf.service.deployment.pack.ServiceDeployPack;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.printers.ColumnPrinter.printer;
import static io.microconfig.osdf.service.deployment.checkers.DeployStatusChecker.deployStatusChecker;
import static io.microconfig.osdf.service.deployment.pack.loader.DefaultServiceDeployPacksLoader.serviceLoader;
import static io.microconfig.osdf.service.loaders.filters.GroupComponentsFilter.groupComponentsFilter;

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

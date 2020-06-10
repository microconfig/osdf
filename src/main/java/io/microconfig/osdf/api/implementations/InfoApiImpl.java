package io.microconfig.osdf.api.implementations;

import io.microconfig.osdf.api.declarations.InfoApi;
import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.commands.LogsCommand;
import io.microconfig.osdf.commands.ShowAllCommand;
import io.microconfig.osdf.commands.StatusCommand;
import io.microconfig.osdf.exceptions.StatusCodeException;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.service.deployment.pack.ServiceDeployPack;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.printers.ColumnPrinter.printer;
import static io.microconfig.osdf.service.deployment.checkers.DeployStatusChecker.deployStatusChecker;
import static io.microconfig.osdf.service.deployment.pack.loader.DefaultServiceDeployPacksLoader.serviceLoader;
import static io.microconfig.osdf.service.loaders.filters.GroupComponentsFilter.groupComponentsFilter;

@RequiredArgsConstructor
public class InfoApiImpl implements InfoApi {
    private final OSDFPaths paths;
    private final ClusterCLI cli;

    public static InfoApi infoApi(OSDFPaths paths, ClusterCLI cli) {
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
        List<ServiceDeployPack> deployPacks = serviceLoader(paths, groupComponentsFilter(paths, group), cli)
                .loadPacks();
        boolean status = deployStatusChecker(timeout == null ? 60 : timeout).check(deployPacks);
        if (!status) throw new StatusCodeException(1);
    }

    @Override
    public void showAll() {
        cli.login();
        new ShowAllCommand(cli, printer()).run();
    }
}

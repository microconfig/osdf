package io.osdf.actions.info.api.status;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.common.exceptions.StatusCodeException;
import io.osdf.settings.paths.OsdfPaths;
import io.osdf.actions.info.printer.ColumnPrinter;
import io.osdf.core.service.core.deployment.pack.ServiceDeployPack;
import io.osdf.core.service.core.job.pack.ServiceJobPack;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.actions.info.api.status.printer.StatusPrinter.statusPrinter;
import static io.osdf.core.service.core.deployment.pack.loader.DefaultServiceDeployPacksLoader.serviceLoader;
import static io.osdf.core.service.core.job.pack.loader.DefaultServiceJobPackLoader.jobLoader;
import static io.osdf.core.service.local.loaders.filters.RequiredComponentsFilter.requiredComponentsFilter;

@RequiredArgsConstructor
public class StatusCommand {
    private final OsdfPaths paths;
    private final ClusterCli cli;
    private final ColumnPrinter printer;
    private final boolean withHealthCheck;

    public void run(List<String> components) {
        if (!checkStatusAndPrint(components)) {
            throw new StatusCodeException(1);
        }
    }

    private boolean checkStatusAndPrint(List<String> serviceNames) {
        List<ServiceJobPack> jobPacks = jobLoader(paths, serviceNames, cli).loadPacks();
        List<ServiceDeployPack> deployPacks = serviceLoader(paths, requiredComponentsFilter(serviceNames), cli).loadPacks();
        return statusPrinter(printer, withHealthCheck).checkStatusAndPrint(deployPacks, jobPacks);
    }
}

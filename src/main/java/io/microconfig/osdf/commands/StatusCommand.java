package io.microconfig.osdf.commands;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.develop.service.deployment.pack.ServiceDeployPack;
import io.microconfig.osdf.develop.service.job.pack.ServiceJobPack;
import io.microconfig.osdf.exceptions.StatusCodeException;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.printers.ColumnPrinter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.develop.service.deployment.pack.loader.DefaultServiceDeployPacksLoader.defaultServiceDeployPacksLoader;
import static io.microconfig.osdf.develop.service.job.pack.loader.DefaultServiceJobPackLoader.defaultServiceJobPackLoader;
import static io.microconfig.osdf.printers.StatusPrinter.statusPrinter;

@RequiredArgsConstructor
public class StatusCommand {
    private final OSDFPaths paths;
    private final ClusterCLI cli;
    private final ColumnPrinter printer;
    private final boolean withHealthCheck;

    public void run(List<String> components) {
        if (!checkStatusAndPrint(components)) {
            throw new StatusCodeException(1);
        }
    }

    private boolean checkStatusAndPrint(List<String> serviceNames) {
        List<ServiceJobPack> jobPacks = defaultServiceJobPackLoader(paths, serviceNames, cli).loadPacks();
        List<ServiceDeployPack> deployPacks = defaultServiceDeployPacksLoader(paths, serviceNames, cli).loadPacks();
        return statusPrinter(printer, withHealthCheck).checkStatusAndPrint(deployPacks, jobPacks);
    }
}

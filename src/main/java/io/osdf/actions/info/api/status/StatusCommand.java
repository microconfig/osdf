package io.osdf.actions.info.api.status;

import io.osdf.actions.info.printer.ColumnPrinter;
import io.osdf.common.exceptions.StatusCodeException;
import io.osdf.core.application.job.JobApplication;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.actions.info.api.status.printer.StatusPrinter.statusPrinter;
import static io.osdf.core.application.job.JobFilter.job;
import static io.osdf.core.application.local.loaders.ApplicationFilesLoaderImpl.activeRequiredAppsLoader;
import static io.osdf.core.application.service.ServiceApplicationMapper.service;

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
        List<ServiceApplication> services = activeRequiredAppsLoader(paths, serviceNames).load(service(cli));
        List<JobApplication> jobs = activeRequiredAppsLoader(paths, serviceNames).load(job(cli));
        return statusPrinter(cli, printer, withHealthCheck).checkStatusAndPrint(services, jobs);
    }
}

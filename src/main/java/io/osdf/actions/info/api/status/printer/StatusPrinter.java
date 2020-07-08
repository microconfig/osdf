package io.osdf.actions.info.api.status.printer;

import io.osdf.actions.info.printer.ColumnPrinter;
import io.osdf.core.application.job.JobApplication;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static io.osdf.actions.info.api.status.printer.DeploymentStatusRows.deploymentStatusRows;
import static io.osdf.actions.info.api.status.printer.JobStatusRow.jobStatusRow;
import static io.osdf.common.utils.ThreadUtils.runInParallel;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class StatusPrinter {
    private final ClusterCli cli;
    private final ColumnPrinter printer;
    private final boolean withHealthCheck;

    public static StatusPrinter statusPrinter(ClusterCli cli, ColumnPrinter printer, boolean withHealthCheck) {
        return new StatusPrinter(cli, printer, withHealthCheck);
    }

    public boolean checkStatusAndPrint(List<ServiceApplication> services, List<JobApplication> jobs) {
        printer.addColumns("COMPONENT", "VERSION", "CONFIGS", "STATUS", "REPLICAS");

        List<Object> apps = concatenate(services, jobs);
        List<RowColumnsWithStatus> statuses = fetchStatuses(apps);
        statuses.forEach(printer::add);
        printer.print();
        return statuses.stream().allMatch(RowColumnsWithStatus::getStatus);
    }

    private List<RowColumnsWithStatus> fetchStatuses(List<Object> services) {
        return runInParallel(services.size(),
                () -> services.parallelStream()
                        .map(this::toRowColumnsWithStatus)
                        .collect(toUnmodifiableList()));
    }

    private RowColumnsWithStatus toRowColumnsWithStatus(Object app) {
        if (app instanceof ServiceApplication) {
            return deploymentStatusRows(cli, (ServiceApplication) app, printer.newPrinter(), withHealthCheck);
        }
        if (app instanceof JobApplication) {
            return jobStatusRow(cli, (JobApplication) app, printer.newPrinter());
        }
        throw new RuntimeException("Unknown component type");
    }

    private List<Object> concatenate(List<ServiceApplication> services, List<JobApplication> jobs) {
        List<Object> apps = new ArrayList<>();
        apps.addAll(jobs);
        apps.addAll(services);
        return apps;
    }
}

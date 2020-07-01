package io.osdf.actions.info.api.status.printer;

import io.osdf.actions.info.printer.ColumnPrinter;
import io.osdf.core.service.core.deployment.pack.ServiceDeployPack;
import io.osdf.core.service.core.job.pack.ServiceJobPack;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static io.osdf.actions.info.api.status.printer.DeploymentStatusRows.deploymentStatusRows;
import static io.osdf.actions.info.api.status.printer.JobStatusRow.jobStatusRow;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class StatusPrinter {
    private final ColumnPrinter printer;
    private final boolean withHealthCheck;

    public static StatusPrinter statusPrinter(ColumnPrinter printer, boolean withHealthCheck) {
        return new StatusPrinter(printer, withHealthCheck);
    }

    public boolean checkStatusAndPrint(List<ServiceDeployPack> deployments, List<ServiceJobPack> jobs) {
        printer.addColumns("COMPONENT", "VERSION", "CONFIGS", "STATUS", "REPLICAS");

        List<Object> services = concatenate(deployments, jobs);
        List<RowColumnsWithStatus> statuses = fetchStatuses(services);
        statuses.forEach(printer::add);
        printer.print();
        return statuses.stream().allMatch(RowColumnsWithStatus::getStatus);
    }

    private List<RowColumnsWithStatus> fetchStatuses(List<Object> services) {
        return services.stream()
                .parallel()
                .map(this::toRowColumnsWithStatus)
                .collect(toUnmodifiableList());
    }

    private RowColumnsWithStatus toRowColumnsWithStatus(Object service) {
        if (service instanceof ServiceDeployPack) {
            ServiceDeployPack deployPack = (ServiceDeployPack) service;
            return deploymentStatusRows(deployPack.deployment(), deployPack.files(), printer.newPrinter(), withHealthCheck);
        }
        if (service instanceof ServiceJobPack) {
            ServiceJobPack jobPack = (ServiceJobPack) service;
            return jobStatusRow(jobPack.job(), printer.newPrinter());
        }
        throw new RuntimeException("Unknown component type");
    }

    private List<Object> concatenate(List<ServiceDeployPack> deployments, List<ServiceJobPack> jobs) {
        List<Object> services = new ArrayList<>();
        services.addAll(jobs);
        services.addAll(deployments);
        return services;
    }
}

package io.microconfig.osdf.printers;

import io.microconfig.osdf.components.AbstractOpenShiftComponent;
import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.components.JobComponent;
import io.microconfig.osdf.components.info.RowColumnsWithStatus;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static io.microconfig.osdf.components.info.DeploymentStatusRows.deploymentStatusRows;
import static io.microconfig.osdf.components.info.JobStatusRow.jobStatusRow;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class StatusPrinter {
    private final List<JobComponent> jobComponents;
    private final List<DeploymentComponent> deploymentComponents;
    private final ColumnPrinter printer;
    private final boolean withHealthCheck;

    public static StatusPrinter statusPrinter(List<JobComponent> jobComponents, List<DeploymentComponent> deploymentComponents,
                                              ColumnPrinter printer, boolean withHealthCheck) {
        return new StatusPrinter(jobComponents, deploymentComponents, printer, withHealthCheck);
    }

    public boolean checkStatusAndPrint() {
        printer.addColumns("COMPONENT", "VERSION", "STATUS", "REPLICAS");

        List<AbstractOpenShiftComponent> components = concatenate(jobComponents, deploymentComponents);
        List<RowColumnsWithStatus> statuses = fetchStatuses(components);
        statuses.forEach(printer::add);
        printer.print();
        return statuses.stream().allMatch(RowColumnsWithStatus::getStatus);
    }

    private List<RowColumnsWithStatus> fetchStatuses(List<AbstractOpenShiftComponent> components) {
        return components.stream()
                .parallel()
                .map(this::toRowColumnsWithStatus)
                .collect(toUnmodifiableList());
    }

    private RowColumnsWithStatus toRowColumnsWithStatus(AbstractOpenShiftComponent component) {
        if (component instanceof DeploymentComponent) {
            return deploymentStatusRows((DeploymentComponent) component, printer.newPrinter(), withHealthCheck);
        }
        if (component instanceof JobComponent) {
            return jobStatusRow((JobComponent) component, printer.newPrinter());
        }
        throw new RuntimeException("Unknown component type");
    }

    private List<AbstractOpenShiftComponent> concatenate(List<JobComponent> jobComponents, List<DeploymentComponent> deploymentComponents) {
        List<AbstractOpenShiftComponent> components = new ArrayList<>();
        components.addAll(jobComponents);
        components.addAll(deploymentComponents);
        return components;
    }
}

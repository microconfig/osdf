package io.osdf.actions.info.api.status.printer;

import io.osdf.actions.info.printer.ColumnPrinter;
import io.osdf.core.application.core.Application;
import io.osdf.core.application.job.JobApplication;
import io.osdf.core.application.plain.PlainApplication;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.actions.info.api.status.printer.JobStatusRowGetter.jobStatusRow;
import static io.osdf.actions.info.api.status.printer.PlainAppStatusRowsGetter.plainAppStatusRowsGetter;
import static io.osdf.actions.info.api.status.printer.ServiceStatusRowsGetter.serviceStatusRows;
import static io.osdf.common.utils.MappingUtils.fromMapping;
import static io.osdf.common.utils.ThreadUtils.runInParallel;
import static java.util.Map.of;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class StatusPrinter {
    private final ClusterCli cli;
    private final ColumnPrinter printer;
    private final boolean withHealthCheck;

    public static StatusPrinter statusPrinter(ClusterCli cli, ColumnPrinter printer, boolean withHealthCheck) {
        return new StatusPrinter(cli, printer, withHealthCheck);
    }

    public boolean checkStatusAndPrint(List<Application> apps) {
        printer.addColumns("COMPONENT", "VERSION", "CONFIGS", "STATUS", "REPLICAS");

        List<RowColumnsWithStatus> statuses = fetchStatuses(apps);
        statuses.forEach(printer::add);
        printer.print();
        return statuses.stream().allMatch(RowColumnsWithStatus::getStatus);
    }

    private List<RowColumnsWithStatus> fetchStatuses(List<Application> services) {
        return runInParallel(services.size(),
                () -> services.parallelStream()
                        .map(this::toRowColumnsWithStatus)
                        .collect(toUnmodifiableList()));
    }

    private RowColumnsWithStatus toRowColumnsWithStatus(Application app) {
        return fromMapping(app, of(
                ServiceApplication.class, () -> serviceStatusRows(cli, withHealthCheck).statusOf(app, printer.newPrinter()),
                JobApplication.class, () -> jobStatusRow(cli).statusOf(app, printer.newPrinter()),
                PlainApplication.class, () -> plainAppStatusRowsGetter().statusOf(app, printer.newPrinter())
        ));
    }
}

package io.osdf.actions.info.api.status.printer;

import io.osdf.actions.info.info.job.JobStatus;
import io.osdf.actions.info.info.job.ServiceJobInfo;
import io.osdf.core.service.core.job.ServiceJob;
import io.osdf.actions.info.printer.ColumnPrinter;

import java.util.List;

import static io.osdf.actions.info.info.job.JobStatus.SUCCEEDED;
import static io.microconfig.utils.ConsoleColor.*;

public class JobStatusRow implements RowColumnsWithStatus {
    private final ServiceJob job;
    private final ColumnPrinter printer;
    private final boolean status;

    public JobStatusRow(ServiceJob job, ColumnPrinter printer) {
        this.job = job;
        this.printer = printer;
        this.status = fetch();
    }

    public static JobStatusRow jobStatusRow(ServiceJob job, ColumnPrinter printer) {
        return new JobStatusRow(job, printer);
    }

    @Override
    public List<String> getColumns() {
        return printer.getColumns();
    }

    @Override
    public List<List<String>> getRows() {
        return printer.getRows();
    }

    @Override
    public boolean getStatus() {
        return status;
    }

    private boolean fetch() {
        ServiceJobInfo info = job.info();
        printer.addRow(green(job.serviceName()),
                green(formatVersions(info.version(), job.version())),
                green(info.configVersion()),
                coloredStatus(info.status()),
                green("-"));
        return info.status() == SUCCEEDED;
    }

    private String formatVersions(String remote, String local) {
        if (remote.equalsIgnoreCase(local)) return remote;
        return remote + " [" + local + "]";
    }

    private String coloredStatus(JobStatus status) {
        String statusString = status.toString().replace("_", " ");
        switch (status) {
            case SUCCEEDED:
                return green(statusString);
            case FAILED:
                return red(statusString);
            case ACTIVE:
                return yellow(statusString);
            default:
                return statusString;
        }
    }
}

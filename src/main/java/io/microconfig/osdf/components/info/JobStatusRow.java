package io.microconfig.osdf.components.info;

import io.microconfig.osdf.develop.service.job.ServiceJob;
import io.microconfig.osdf.develop.service.job.info.ServiceJobInfo;
import io.microconfig.osdf.printers.ColumnPrinter;

import java.util.List;

import static io.microconfig.osdf.components.info.JobStatus.SUCCEEDED;
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
        printer.addRow(green(job.serviceName()), green(job.version()), coloredStatus(info.status()), green("-"));
        return info.status() == SUCCEEDED;
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

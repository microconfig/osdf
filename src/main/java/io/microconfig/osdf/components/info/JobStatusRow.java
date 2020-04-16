package io.microconfig.osdf.components.info;

import io.microconfig.osdf.components.JobComponent;
import io.microconfig.osdf.printers.ColumnPrinter;

import java.util.List;

import static io.microconfig.osdf.components.info.JobStatus.SUCCEEDED;
import static io.microconfig.utils.ConsoleColor.*;

public class JobStatusRow implements RowColumnsWithStatus {
    private final JobComponent component;
    private final ColumnPrinter printer;
    private final boolean status;

    public JobStatusRow(JobComponent component, ColumnPrinter printer) {
        this.component = component;
        this.printer = printer;
        this.status = fetch();
    }

    public static JobStatusRow jobStatusRow(JobComponent component, ColumnPrinter printer) {
        return new JobStatusRow(component, printer);
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
        JobInfo info = component.info();
        printer.addRow(green(component.getName()), green(component.getVersion()), coloredStatus(info.getStatus()), green("-"));
        return info.getStatus() == SUCCEEDED;
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

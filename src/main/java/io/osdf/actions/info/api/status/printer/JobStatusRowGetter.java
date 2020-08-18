package io.osdf.actions.info.api.status.printer;

import io.osdf.actions.info.api.status.AppStatusRowsGetter;
import io.osdf.actions.info.printer.ColumnPrinter;
import io.osdf.actions.info.status.job.JobStatus;
import io.osdf.core.application.core.Application;
import io.osdf.core.application.core.description.CoreDescription;
import io.osdf.core.application.job.JobApplication;
import io.osdf.core.connection.cli.ClusterCli;

import java.util.Optional;

import static io.microconfig.utils.ConsoleColor.*;
import static io.osdf.actions.info.api.status.printer.RowColumnsWithStatusImpl.rowColumnsWithStatus;
import static io.osdf.actions.info.api.status.printer.StatusRowsFormatter.formatter;
import static io.osdf.actions.info.status.job.JobStatus.NOT_EXECUTED;
import static io.osdf.actions.info.status.job.JobStatus.SUCCEEDED;
import static io.osdf.actions.info.status.job.JobStatusGetter.jobStatusGetter;

public class JobStatusRowGetter implements AppStatusRowsGetter {
    private final ClusterCli cli;

    public JobStatusRowGetter(ClusterCli cli) {
        this.cli = cli;
    }

    public static JobStatusRowGetter jobStatusRow(ClusterCli cli) {
        return new JobStatusRowGetter(cli);
    }

    @Override
    public RowColumnsWithStatus statusOf(Application app, ColumnPrinter printer) {
        JobApplication jobApp = (JobApplication) app;
        return rowColumnsWithStatus(printer, addRowsToPrinterAndReturnStatus(jobApp, formatter(printer)));
    }

    private boolean addRowsToPrinterAndReturnStatus(JobApplication jobApp, StatusRowsFormatter formatter) {
        Optional<CoreDescription> coreDescription = jobApp.coreDescription();
        if (coreDescription.isEmpty()) {
            formatter.addNotFoundRow(jobApp.files(), coloredStatus(NOT_EXECUTED));
            return false;
        }

        JobStatus jobStatus = jobStatusGetter(cli).statusOf(jobApp);
        formatter.addMainRow(jobApp.files(), coreDescription.get(), coloredStatus(jobStatus), "-");
        return jobStatus == SUCCEEDED;
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

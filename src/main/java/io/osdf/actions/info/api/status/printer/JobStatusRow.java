package io.osdf.actions.info.api.status.printer;

import io.osdf.actions.info.printer.ColumnPrinter;
import io.osdf.actions.info.status.job.JobStatus;
import io.osdf.common.yaml.YamlObject;
import io.osdf.core.application.job.JobApplication;
import io.osdf.core.cluster.job.ClusterJob;
import io.osdf.core.connection.cli.ClusterCli;

import java.util.List;

import static io.microconfig.utils.ConsoleColor.*;
import static io.osdf.actions.info.status.job.JobStatus.NOT_EXECUTED;
import static io.osdf.actions.info.status.job.JobStatus.SUCCEEDED;
import static io.osdf.actions.info.status.job.JobStatusGetter.jobStatusGetter;
import static io.osdf.common.yaml.YamlObject.yaml;

public class JobStatusRow implements RowColumnsWithStatus {
    private final ClusterCli cli;
    private final JobApplication jobApp;
    private final ColumnPrinter printer;
    private final boolean status;

    public JobStatusRow(ClusterCli cli, JobApplication jobApp, ColumnPrinter printer) {
        this.cli = cli;
        this.jobApp = jobApp;
        this.printer = printer;
        this.status = fetch();
    }

    public static JobStatusRow jobStatusRow(ClusterCli cli, JobApplication jobApp, ColumnPrinter printer) {
        return new JobStatusRow(cli, jobApp, printer);
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
        if (!jobApp.exists()) {
            addNotFoundRow();
            return false;
        }

        ClusterJob job = jobApp.job();
        JobStatus status = jobStatusGetter(cli).statusOf(jobApp);
        YamlObject yaml = yaml(jobApp.files().getPath("deploy.yaml"));
        printer.addRow(green(jobApp.files().name()),
                green(formatVersions(jobApp.coreDescription().getAppVersion(), yaml.get("app.version"))),
                green(formatVersions(jobApp.coreDescription().getConfigVersion(), yaml.get("config.version"))),
                coloredStatus(status),
                green("-"));
        return status == SUCCEEDED;
    }

    private void addNotFoundRow() {
        YamlObject yaml = yaml(jobApp.files().getPath("deploy.yaml"));
        printer.addRow(green(jobApp.files().name()),
                green(formatVersions("-", yaml.get("app.version"))),
                green(formatVersions("-", yaml.get("config.version"))),
                coloredStatus(NOT_EXECUTED),
                green("-"));
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
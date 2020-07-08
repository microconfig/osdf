package io.osdf.actions.management.deploy.jobrunner;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.job.JobApplication;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import static io.microconfig.utils.Logger.info;
import static io.osdf.actions.management.deploy.cleaner.ResourceDeleter.resourceDeleter;
import static io.osdf.common.yaml.YamlObject.yaml;
import static java.nio.file.Path.of;
import static java.util.Objects.requireNonNullElse;

@RequiredArgsConstructor
public class JobRunnerImpl implements JobRunner {
    private final ClusterCli cli;

    public static JobRunnerImpl jobRunner(ClusterCli cli) {
        return new JobRunnerImpl(cli);
    }

    @Override
    public void runJob(JobApplication jobApp) {
        cleanResources(jobApp);
        jobApp.uploadDescription();
        uploadResources(jobApp);
        if (!waitUntilCompleted(jobApp)) {
            throw new OSDFException("Job " + jobApp.files().name() + " failed");
        }
    }

    private void uploadResources(JobApplication jobApp) {
        cli.execute("apply -f " + jobApp.files().getPath("resources"))
                .throwExceptionIfError();
    }

    private void cleanResources(JobApplication application) {
        if (application.exists()) {
            resourceDeleter(cli)
                    .deleteOldResources(application.coreDescription(), application.files())
                    .deleteConfigMaps(application.coreDescription());
        }
        String jobName = application.files().metadata().getMainResource().getName();
        cli.execute("delete job " + jobName);
    }

    private boolean waitUntilCompleted(JobApplication jobApp) {
        int waitSec = getWaitTimeout(jobApp);
        info(jobApp.name() + " timeout: " + waitSec + "s");
        return cli.execute("wait --for=condition=complete --timeout=" + waitSec + "s job/" + jobApp.job().name())
                .ok();
    }

    private int getWaitTimeout(JobApplication jobApp) {
        Integer timeout = yaml(of(jobApp.files().metadata().getMainResource().getPath()))
                .get("spec.activeDeadlineSeconds");
        return requireNonNullElse(timeout, 60);
    }
}

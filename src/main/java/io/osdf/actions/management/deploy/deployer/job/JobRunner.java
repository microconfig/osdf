package io.osdf.actions.management.deploy.deployer.job;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.job.JobApplication;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import static io.microconfig.utils.Logger.error;
import static io.osdf.actions.management.deploy.deployer.ResourceDeleter.resourceDeleter;

@RequiredArgsConstructor
public class JobRunner {
    private final ClusterCli cli;

    public static JobRunner jobRunner(ClusterCli cli) {
        return new JobRunner(cli);
    }

    public boolean runJob(JobApplication jobApp) {
        try {
            cleanResources(jobApp);
            jobApp.uploadDescription();
            uploadResources(jobApp);
        } catch (OSDFException e) {
            error(e.getMessage());
            return false;
        }
        return true;
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
}

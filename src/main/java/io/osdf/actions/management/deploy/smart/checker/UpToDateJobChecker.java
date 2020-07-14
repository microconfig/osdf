package io.osdf.actions.management.deploy.smart.checker;

import io.osdf.actions.management.deploy.smart.hash.ResourcesHashComputer;
import io.osdf.core.application.core.Application;
import io.osdf.core.application.job.JobApplication;
import io.osdf.core.cluster.resource.ClusterResourceImpl;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import static io.osdf.actions.info.status.job.JobStatus.SUCCEEDED;
import static io.osdf.actions.info.status.job.JobStatusGetter.jobStatusGetter;
import static io.osdf.actions.management.deploy.smart.hash.ResourcesHashComputer.resourcesHashComputer;
import static io.osdf.core.application.job.JobApplication.jobApplication;

@RequiredArgsConstructor
public class UpToDateJobChecker implements UpToDateChecker {
    private final ClusterCli cli;
    private final ResourcesHashComputer resourcesHashComputer = resourcesHashComputer();

    public static UpToDateJobChecker upToDateJobChecker(ClusterCli cli) {
        return new UpToDateJobChecker(cli);
    }

    @Override
    public boolean check(Application app) {
        JobApplication jobApp = jobApplication(app);

        if (jobStatusGetter(cli).statusOf(jobApp) != SUCCEEDED) return false;
        String configHash = new ClusterResourceImpl("job", jobApp.job().name()).label("configHash", cli);
        return configHash.equals(resourcesHashComputer.currentHash(jobApp.files()));
    }
}

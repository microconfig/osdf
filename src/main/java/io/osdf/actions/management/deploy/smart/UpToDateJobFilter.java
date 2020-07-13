package io.osdf.actions.management.deploy.smart;

import io.osdf.actions.management.deploy.smart.hash.ResourcesHashComputer;
import io.osdf.core.application.job.JobApplication;
import io.osdf.core.cluster.resource.ClusterResourceImpl;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.actions.info.status.job.JobStatus.SUCCEEDED;
import static io.osdf.actions.info.status.job.JobStatusGetter.jobStatusGetter;
import static io.osdf.actions.management.deploy.smart.hash.ResourcesHashComputer.resourcesHashComputer;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class UpToDateJobFilter {
    private final ClusterCli cli;
    private final ResourcesHashComputer resourcesHashComputer = resourcesHashComputer();

    public static UpToDateJobFilter upToDateJobFilter(ClusterCli cli) {
        return new UpToDateJobFilter(cli);
    }

    public List<JobApplication> filter(List<JobApplication> jobs) {
        return jobs.parallelStream()
                .filter(service -> !isUpToDate(service))
                .collect(toUnmodifiableList());
    }

    public boolean isUpToDate(JobApplication jobApp) {
        if (jobStatusGetter(cli).statusOf(jobApp) != SUCCEEDED) return false;
        String configHash = new ClusterResourceImpl("job", jobApp.job().name()).label("configHash", cli);
        return configHash.equals(resourcesHashComputer.currentHash(jobApp.files()));
    }

}

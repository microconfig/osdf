package io.osdf.actions.management.deploy;

import io.osdf.actions.management.deploy.jobrunner.JobRunnerImpl;
import io.osdf.actions.management.deploy.smart.hash.ResourcesHashComputer;
import io.osdf.core.application.job.JobApplication;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.utils.Logger.announce;
import static io.osdf.actions.management.deploy.jobrunner.JobRunnerImpl.jobRunner;
import static io.osdf.actions.management.deploy.smart.UpToDateJobFilter.upToDateJobFilter;
import static io.osdf.actions.management.deploy.smart.hash.ResourcesHashComputer.resourcesHashComputer;
import static io.osdf.actions.management.deploy.smart.image.ImageTagReplacer.imageTagReplacer;
import static io.osdf.common.utils.ThreadUtils.runInParallel;
import static io.osdf.core.application.job.JobFilter.job;
import static io.osdf.core.application.local.loaders.ApplicationFilesLoaderImpl.activeRequiredAppsLoader;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class RunJobsCommand {
    private final OsdfPaths paths;
    private final ClusterCli cli;

    public static RunJobsCommand runJobsCommand(OsdfPaths paths, ClusterCli cli) {
        return new RunJobsCommand(paths, cli);
    }

    public boolean run(List<String> serviceNames, Boolean smart) {
        List<JobApplication> allJobs = activeRequiredAppsLoader(paths, serviceNames).load(job(cli));
        if (allJobs.isEmpty()) return true;

        preprocessJobs(allJobs);
        List<JobApplication> jobsToRun = getJobsToRun(smart, allJobs);
        if (jobsToRun.isEmpty()) return true;

        JobRunnerImpl jobRunner = jobRunner(cli);
        List<Boolean> result = runInParallel(jobsToRun.size(),
                () -> jobsToRun.parallelStream()
                        .map(jobRunner::runJob)
                        .collect(toUnmodifiableList())
        );
        return result.stream().allMatch(t -> t);
    }

    private List<JobApplication> getJobsToRun(Boolean smart, List<JobApplication> allJobs) {
        List<JobApplication> jobsToRun = smart ? upToDateJobFilter(cli).filter(allJobs) : allJobs;
        if (jobsToRun.isEmpty()) {
            announce("All jobs are up-to-date");
        } else {
            announce("Deploying: " +
                    jobsToRun.stream()
                            .map(service -> service.files().name())
                            .collect(joining(" ")));
        }
        return jobsToRun;
    }

    private void preprocessJobs(List<JobApplication> jobs) {
        replaceTag(jobs);
        insertHashes(jobs);
    }

    private void replaceTag(List<JobApplication> jobs) {
        jobs.forEach(job -> imageTagReplacer(paths).replaceFor(job.files()));
    }

    private void insertHashes(List<JobApplication> jobs) {
        ResourcesHashComputer resourcesHashComputer = resourcesHashComputer();
        jobs.forEach(job -> resourcesHashComputer.insertIn(job.files()));
    }
}

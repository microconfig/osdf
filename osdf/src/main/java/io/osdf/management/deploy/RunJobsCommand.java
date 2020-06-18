package io.osdf.management.deploy;

import io.cluster.old.cluster.cli.ClusterCli;
import io.microconfig.osdf.jobrunners.DefaultJobRunner;
import io.osdf.settings.paths.OsdfPaths;
import io.microconfig.osdf.resources.ResourceHash;
import io.microconfig.osdf.service.job.pack.ServiceJobPack;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.jobrunners.DefaultJobRunner.defaultJobRunner;
import static io.microconfig.osdf.resources.ResourceHash.jobHash;
import static io.microconfig.osdf.service.job.pack.loader.DefaultServiceJobPackLoader.jobLoader;
import static io.microconfig.osdf.service.job.tools.UpToDateJobFilter.upToDateJobFilter;
import static io.microconfig.utils.Logger.announce;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

@RequiredArgsConstructor
public class RunJobsCommand {
    private final OsdfPaths paths;
    private final ClusterCli cli;

    public static RunJobsCommand runJobsCommand(OsdfPaths paths, ClusterCli cli) {
        return new RunJobsCommand(paths, cli);
    }

    public void run(List<String> serviceNames, Boolean smart) {
        List<ServiceJobPack> jobPacks = getJobPacks(serviceNames, smart);
        callRunner(jobPacks);
    }

    private List<ServiceJobPack> getJobPacks(List<String> serviceNames, boolean smart) {
        List<ServiceJobPack> allPacks = jobLoader(paths, serviceNames, cli).loadPacks();

        ResourceHash resourceHash = jobHash(paths);
        allPacks.forEach(pack -> resourceHash.insert(pack.files()));

        List<ServiceJobPack> jobPacks = smart ? upToDateJobFilter(cli).filter(allPacks, resourceHash) : allPacks;
        if (jobPacks.isEmpty())  {
            announce("All executed jobs are up-to-date");
        } else {
            announce("Running: " +
                    jobPacks.stream()
                            .map(jobPack -> jobPack.service().name())
                            .collect(joining(" ")));
        }
        return jobPacks;
    }

    private void callRunner(List<ServiceJobPack> jobPacks) {
        DefaultJobRunner runner = defaultJobRunner();
        range(0, jobPacks.size()).forEach(i -> runner.run(
                jobPacks.get(i).service(),
                jobPacks.get(i).job(),
                jobPacks.get(i).files())
        );
    }
}

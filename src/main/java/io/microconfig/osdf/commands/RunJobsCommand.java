package io.microconfig.osdf.commands;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.jobrunners.DefaultJobRunner;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.resources.ResourceHash;
import io.microconfig.osdf.service.job.info.ServiceJobInfo;
import io.microconfig.osdf.service.job.pack.ServiceJobPack;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.jobrunners.DefaultJobRunner.defaultJobRunner;
import static io.microconfig.osdf.resources.ResourceHash.jobHash;
import static io.microconfig.osdf.service.job.info.JobStatus.SUCCEEDED;
import static io.microconfig.osdf.service.job.pack.loader.DefaultServiceJobPackLoader.jobLoader;
import static io.microconfig.osdf.service.job.tools.UpToDateJobFilter.upToDateJobFilter;
import static io.microconfig.utils.Logger.announce;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

@RequiredArgsConstructor
public class RunJobsCommand {
    private final OSDFPaths paths;
    private final ClusterCLI cli;

    public static RunJobsCommand runJobsCommand(OSDFPaths paths, ClusterCLI cli) {
        return new RunJobsCommand(paths, cli);
    }

    public void run(List<String> serviceNames, Boolean smart) {
        List<ServiceJobPack> jobPacks = getJobPacks(serviceNames, smart);
        callRunner(jobPacks);
    }

    private List<ServiceJobPack> getJobPacks(List<String> serviceNames, Boolean smart) {
        List<ServiceJobPack> allPacks = jobLoader(paths, serviceNames, cli).loadPacks();

        ResourceHash resourceHash = jobHash(paths);
        allPacks.forEach(pack -> resourceHash.insert(pack.files(), !smart));

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

    private boolean jobNeedsUpdate(ServiceJobPack jobPack) {
        if (!jobPack.job().exists()) return true;
        ServiceJobInfo info = jobPack.job().info();
        return info.status() != SUCCEEDED || !info.version().equals(jobPack.service().version());
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

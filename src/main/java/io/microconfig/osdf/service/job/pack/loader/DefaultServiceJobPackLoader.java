package io.microconfig.osdf.service.job.pack.loader;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.service.ClusterService;
import io.microconfig.osdf.service.files.ServiceFiles;
import io.microconfig.osdf.service.job.ServiceJob;
import io.microconfig.osdf.service.job.matchers.ServiceJobMatcher;
import io.microconfig.osdf.service.job.pack.ServiceJobPack;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.service.DefaultClusterService.defaultClusterService;
import static io.microconfig.osdf.service.job.matchers.ServiceJobMatcher.serviceJobMatcher;
import static io.microconfig.osdf.service.job.pack.DefaultServiceJobPack.defaultServiceJobPack;
import static io.microconfig.osdf.service.loaders.DefaultServiceFilesLoader.servicesLoader;
import static java.nio.file.Files.exists;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.IntStream.range;

@RequiredArgsConstructor
public class DefaultServiceJobPackLoader implements ServiceJobPackLoader {
    private final OSDFPaths paths;
    private final List<String> requiredJobsNames;
    private final ClusterCLI cli;

    public static DefaultServiceJobPackLoader defaultServiceJobPackLoader(OSDFPaths paths, List<String> requiredJobsNames, ClusterCLI cli) {
        return new DefaultServiceJobPackLoader(paths, requiredJobsNames, cli);
    }

    @Override
    public List<ServiceJobPack> loadPacks() {
        List<ServiceFiles> serviceFilesList = servicesLoader(paths, requiredJobsNames, this::isJobService).load();
        List<ServiceJob> jobs = jobsFromServiceFiles(serviceFilesList);
        List<ClusterService> services = servicesFromJobs(jobs);

        return range(0, services.size())
                .mapToObj(i -> defaultServiceJobPack(serviceFilesList.get(i), jobs.get(i), services.get(i)))
                .collect(toUnmodifiableList());
    }

    private List<ServiceJob> jobsFromServiceFiles(List<ServiceFiles> serviceFilesList) {
        ServiceJobMatcher matcher = serviceJobMatcher(cli);
        return serviceFilesList.stream()
                .map(matcher::match)
                .collect(toUnmodifiableList());
    }

    private List<ClusterService> servicesFromJobs(List<ServiceJob> jobs) {
        return jobs.stream()
                .map(job -> defaultClusterService(job.serviceName(), job.version(), cli))
                .collect(toUnmodifiableList());
    }

    private boolean isJobService(ServiceFiles files) {
        return exists(files.getPath("resources/job.yaml"));
    }
}

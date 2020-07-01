package io.osdf.core.service.core.job.pack.loader;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import io.osdf.core.service.cluster.ClusterService;
import io.osdf.core.service.local.ServiceFiles;
import io.osdf.core.service.core.job.ServiceJob;
import io.osdf.core.service.core.job.ServiceJobMatcher;
import io.osdf.core.service.core.job.pack.ServiceJobPack;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.core.service.cluster.types.DefaultClusterService.defaultClusterService;
import static io.osdf.core.service.core.job.ServiceJobMatcher.serviceJobMatcher;
import static io.osdf.core.service.core.job.pack.DefaultServiceJobPack.defaultServiceJobPack;
import static io.osdf.core.service.local.loaders.DefaultServiceFilesLoader.activeServicesLoader;
import static io.osdf.core.service.local.loaders.filters.RequiredComponentsFilter.requiredComponentsFilter;
import static java.nio.file.Files.exists;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.IntStream.range;

@RequiredArgsConstructor
public class DefaultServiceJobPackLoader implements ServiceJobPackLoader {
    private final OsdfPaths paths;
    private final List<String> requiredJobsNames;
    private final ClusterCli cli;

    public static DefaultServiceJobPackLoader jobLoader(OsdfPaths paths, List<String> requiredJobsNames, ClusterCli cli) {
        return new DefaultServiceJobPackLoader(paths, requiredJobsNames, cli);
    }

    @Override
    public List<ServiceJobPack> loadPacks() {
        List<ServiceFiles> serviceFilesList = activeServicesLoader(paths)
                .withDirFilter(requiredComponentsFilter(requiredJobsNames))
                .withServiceFilter(this::isJobService)
                .load();
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

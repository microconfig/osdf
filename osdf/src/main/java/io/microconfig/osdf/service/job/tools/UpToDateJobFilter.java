package io.microconfig.osdf.service.job.tools;

import io.cluster.old.cluster.cli.ClusterCli;
import io.cluster.old.cluster.resource.ClusterResourceImpl;
import io.microconfig.osdf.resources.ResourceHash;
import io.microconfig.osdf.service.job.ServiceJob;
import io.microconfig.osdf.service.job.pack.ServiceJobPack;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.service.job.info.JobStatus.SUCCEEDED;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class UpToDateJobFilter {
    private final ClusterCli cli;

    public static UpToDateJobFilter upToDateJobFilter(ClusterCli cli) {
        return new UpToDateJobFilter(cli);
    }

    public List<ServiceJobPack> filter(List<ServiceJobPack> services, ResourceHash resourceHash) {
        return services.parallelStream()
                .filter(service -> !isUpToDate(service, resourceHash))
                .collect(toUnmodifiableList());
    }

    public boolean isUpToDate(ServiceJobPack jobPack, ResourceHash resourceHash) {
        return totalHashIsRecent(resourceHash.currentHash(jobPack.files()), jobPack.job());
    }

    private boolean totalHashIsRecent(String hash, ServiceJob job) {
        if (job.info().status() != SUCCEEDED) return false;
        String configHash = new ClusterResourceImpl("job", job.name()).label(cli, "configHash");
        return configHash.equals(hash);
    }
}

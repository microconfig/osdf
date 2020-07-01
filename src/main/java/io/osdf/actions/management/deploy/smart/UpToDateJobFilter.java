package io.osdf.actions.management.deploy.smart;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.cluster.resource.ClusterResourceImpl;
import io.osdf.actions.management.deploy.smart.hash.ResourceHash;
import io.osdf.core.service.core.job.ServiceJob;
import io.osdf.core.service.core.job.pack.ServiceJobPack;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.actions.info.info.job.JobStatus.SUCCEEDED;
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

package io.osdf.core.service.core.job.pack;

import io.osdf.core.service.cluster.ClusterService;
import io.osdf.core.service.local.ServiceFiles;
import io.osdf.core.service.core.job.ServiceJob;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultServiceJobPack implements ServiceJobPack {
    private final ServiceFiles files;
    private final ServiceJob job;
    private final ClusterService service;

    public static DefaultServiceJobPack defaultServiceJobPack(ServiceFiles files, ServiceJob job,
                                                              ClusterService service) {
        return new DefaultServiceJobPack(files, job, service);
    }

    @Override
    public ServiceFiles files() {
        return files;
    }

    @Override
    public ServiceJob job() {
        return job;
    }

    @Override
    public ClusterService service() {
        return service;
    }
}

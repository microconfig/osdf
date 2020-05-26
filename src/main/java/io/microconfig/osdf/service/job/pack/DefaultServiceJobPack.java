package io.microconfig.osdf.service.job.pack;

import io.microconfig.osdf.service.ClusterService;
import io.microconfig.osdf.service.files.ServiceFiles;
import io.microconfig.osdf.service.job.ServiceJob;
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

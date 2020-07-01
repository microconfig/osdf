package io.osdf.core.service.core.job.pack;

import io.osdf.core.service.cluster.ClusterService;
import io.osdf.core.service.local.ServiceFiles;
import io.osdf.core.service.core.job.ServiceJob;

public interface ServiceJobPack {
    ServiceFiles files();

    ServiceJob job();

    ClusterService service();
}

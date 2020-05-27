package io.microconfig.osdf.service.job.pack;

import io.microconfig.osdf.service.ClusterService;
import io.microconfig.osdf.service.files.ServiceFiles;
import io.microconfig.osdf.service.job.ServiceJob;

public interface ServiceJobPack {
    ServiceFiles files();

    ServiceJob job();

    ClusterService service();
}

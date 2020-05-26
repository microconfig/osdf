package io.microconfig.osdf.develop.service.job.pack;

import io.microconfig.osdf.develop.service.ClusterService;
import io.microconfig.osdf.develop.service.files.ServiceFiles;
import io.microconfig.osdf.develop.service.job.ServiceJob;

public interface ServiceJobPack {
    ServiceFiles files();

    ServiceJob job();

    ClusterService service();
}

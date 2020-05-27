package io.microconfig.osdf.jobrunners;

import io.microconfig.osdf.service.ClusterService;
import io.microconfig.osdf.service.files.ServiceFiles;
import io.microconfig.osdf.service.job.ServiceJob;

public interface JobRunner {
    void run(ClusterService service, ServiceJob job, ServiceFiles files);
}

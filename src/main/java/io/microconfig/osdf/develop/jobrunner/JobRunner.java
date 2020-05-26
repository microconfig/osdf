package io.microconfig.osdf.develop.jobrunner;

import io.microconfig.osdf.develop.service.ClusterService;
import io.microconfig.osdf.develop.service.files.ServiceFiles;
import io.microconfig.osdf.develop.service.job.ServiceJob;

public interface JobRunner {
    void run(ClusterService service, ServiceJob job, ServiceFiles files);
}

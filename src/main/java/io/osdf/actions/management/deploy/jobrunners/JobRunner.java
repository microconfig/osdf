package io.osdf.actions.management.deploy.jobrunners;

import io.osdf.core.service.cluster.ClusterService;
import io.osdf.core.service.local.ServiceFiles;
import io.osdf.core.service.core.job.ServiceJob;

public interface JobRunner {
    void run(ClusterService service, ServiceJob job, ServiceFiles files);
}

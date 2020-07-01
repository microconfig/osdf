package io.osdf.actions.management.deploy.jobrunners;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.service.cluster.ClusterService;
import io.osdf.core.service.local.ServiceFiles;
import io.osdf.core.service.core.job.ServiceJob;

import static io.microconfig.utils.Logger.announce;

public class DefaultJobRunner implements JobRunner {
    public static DefaultJobRunner defaultJobRunner() {
        return new DefaultJobRunner();
    }

    @Override
    public void run(ClusterService service, ServiceJob job, ServiceFiles files) {
        announce("Running " + service.name());
        if (job.exists()) {
            job.delete();
        }
        job.createConfigMap(files.configs());
        service.upload(files.resources());

        if (!job.waitUntilCompleted()) {
            throw new OSDFException("Job " + job.serviceName() + " failed");
        }
    }
}

package io.microconfig.osdf.jobrunners;

import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.service.ClusterService;
import io.microconfig.osdf.service.files.ServiceFiles;
import io.microconfig.osdf.service.job.ServiceJob;

import static io.microconfig.osdf.service.job.info.JobStatus.SUCCEEDED;
import static io.microconfig.utils.Logger.announce;

public class DefaultJobRunner implements JobRunner {
    public static DefaultJobRunner defaultJobRunner() {
        return new DefaultJobRunner();
    }

    @Override
    public void run(ClusterService service, ServiceJob job, ServiceFiles files) {
        if (job.exists() && job.info().status() == SUCCEEDED) return;
        announce("Running " + service.name());
        job.delete();
        job.createConfigMap(files.configs());
        service.upload(files.resources());

        if (!job.waitUntilCompleted()) {
            throw new OSDFException("Job " + job.serviceName() + " failed");
        }
    }
}

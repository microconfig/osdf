package io.osdf.actions.management.deploy.jobrunner;

import io.osdf.core.application.job.JobApplication;

public interface JobRunner {
    void runJob(JobApplication jobApp);
}

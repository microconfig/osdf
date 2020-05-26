package io.microconfig.osdf.develop.service.job.info;

import io.microconfig.osdf.components.info.JobStatus;

public interface ServiceJobInfo {
    String version();

    String configVersion();

    JobStatus status();
}

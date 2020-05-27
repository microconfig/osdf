package io.microconfig.osdf.service.job.info;

public interface ServiceJobInfo {
    String version();

    String configVersion();

    JobStatus status();
}

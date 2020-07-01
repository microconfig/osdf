package io.osdf.actions.info.info.job;

public interface ServiceJobInfo {
    String version();

    String configVersion();

    JobStatus status();
}

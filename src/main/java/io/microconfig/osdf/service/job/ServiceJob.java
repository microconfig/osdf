package io.microconfig.osdf.service.job;

import io.microconfig.osdf.cluster.job.ClusterJob;
import io.microconfig.osdf.service.job.info.ServiceJobInfo;
import io.microconfig.osdf.service.ServiceResource;

import java.nio.file.Path;
import java.util.List;

public interface ServiceJob extends ClusterJob, ServiceResource {
    void createConfigMap(List<Path> configs);

    boolean waitUntilCompleted();

    ServiceJobInfo info();
}

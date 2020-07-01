package io.osdf.core.service.core.job;

import io.osdf.core.cluster.job.ClusterJob;
import io.osdf.actions.info.info.job.ServiceJobInfo;
import io.osdf.core.service.core.ServiceResource;

import java.nio.file.Path;
import java.util.List;

public interface ServiceJob extends ClusterJob, ServiceResource {
    void createConfigMap(List<Path> configs);

    boolean waitUntilCompleted();

    ServiceJobInfo info();
}

package io.microconfig.osdf.develop.service.job;

import io.microconfig.osdf.develop.cluster.job.ClusterJob;
import io.microconfig.osdf.develop.service.job.info.ServiceJobInfo;
import io.microconfig.osdf.develop.service.ServiceResource;

import java.nio.file.Path;
import java.util.List;

public interface ServiceJob extends ClusterJob, ServiceResource {
    boolean createConfigMap(List<Path> configs);

    boolean waitUntilCompleted();

    ServiceJobInfo info();
}

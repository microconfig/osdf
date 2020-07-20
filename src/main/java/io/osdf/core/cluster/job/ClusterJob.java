package io.osdf.core.cluster.job;

import io.osdf.core.cluster.resource.ClusterResource;

public interface ClusterJob {
    String name();

    boolean exists();

    void delete();

    ClusterResource toResource();
}

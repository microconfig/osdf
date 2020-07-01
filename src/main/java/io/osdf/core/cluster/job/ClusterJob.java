package io.osdf.core.cluster.job;

public interface ClusterJob {
    String name();

    boolean exists();

    void delete();
}

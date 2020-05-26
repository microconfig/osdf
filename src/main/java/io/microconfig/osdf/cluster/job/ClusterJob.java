package io.microconfig.osdf.cluster.job;

public interface ClusterJob {
    String name();

    boolean exists();

    void delete();
}

package io.microconfig.osdf.develop.cluster.job;

public interface ClusterJob {
    String name();

    boolean exists();

    void delete();
}

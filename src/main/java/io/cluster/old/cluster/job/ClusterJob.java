package io.cluster.old.cluster.job;

public interface ClusterJob {
    String name();

    boolean exists();

    void delete();
}

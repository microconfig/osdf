package io.osdf.core.cluster.resource;

import io.osdf.core.connection.cli.ClusterCli;

public interface ClusterResource {
    String kind();

    String name();

    boolean exists(ClusterCli cli);

    String label(String key, ClusterCli cli);

    void delete(ClusterCli cli);
}

package io.osdf.core.cluster.resource;

import io.osdf.core.connection.cli.ClusterCli;

public interface ClusterResource {
    String kind();

    String name();

    String label(ClusterCli cli, String key);

    void delete(ClusterCli cli);
}

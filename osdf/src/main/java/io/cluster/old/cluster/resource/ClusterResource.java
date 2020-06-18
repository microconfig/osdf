package io.cluster.old.cluster.resource;

import io.cluster.old.cluster.cli.ClusterCli;

public interface ClusterResource {
    String kind();

    String name();

    String label(ClusterCli cli, String key);

    void delete(ClusterCli cli);
}

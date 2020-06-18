package io.cluster.old.cluster.resource;

import io.cluster.old.cluster.cli.ClusterCLI;

public interface ClusterResource {
    String kind();

    String name();

    String label(ClusterCLI cli, String key);

    void delete(ClusterCLI cli);
}

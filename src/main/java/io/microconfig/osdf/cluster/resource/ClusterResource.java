package io.microconfig.osdf.cluster.resource;

import io.microconfig.osdf.cluster.cli.ClusterCLI;

public interface ClusterResource {
    String kind();

    String name();

    String label(ClusterCLI cli, String key);

    void delete(ClusterCLI cli);
}

package io.microconfig.osdf.cluster.resource;

import io.microconfig.osdf.cluster.cli.ClusterCLI;

public interface ClusterResource {
    String kind();

    String name();

    void delete(ClusterCLI cli);
}

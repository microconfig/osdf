package io.microconfig.osdf.develop.cluster;

import io.microconfig.osdf.cluster.cli.ClusterCLI;

public interface ClusterResource {
    String kind();

    String name();

    void delete(ClusterCLI cli);
}

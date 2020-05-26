package io.microconfig.osdf.cluster.resource;

import io.microconfig.osdf.cluster.cli.ClusterCLI;

import java.nio.file.Path;

public interface LocalClusterResource extends ClusterResource, Comparable<LocalClusterResource> {
    Path path();

    void upload(ClusterCLI cli);
}

package io.microconfig.osdf.develop.cluster.resource;

import io.microconfig.osdf.cluster.cli.ClusterCLI;

import java.nio.file.Path;

public interface LocalClusterResource extends ClusterResource, Comparable<LocalClusterResource> {
    Path path();

    void upload(ClusterCLI cli);
}

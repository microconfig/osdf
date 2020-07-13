package io.osdf.core.cluster.resource;

import io.osdf.core.connection.cli.ClusterCli;

import java.nio.file.Path;

public interface LocalClusterResource extends ClusterResource, Comparable<LocalClusterResource> {
    Path path();

    void upload(ClusterCli cli);
}

package io.cluster.old.cluster.resource;

import io.cluster.old.cluster.cli.ClusterCli;

import java.nio.file.Path;

public interface LocalClusterResource extends ClusterResource, Comparable<LocalClusterResource> {
    Path path();

    void upload(ClusterCli cli);
}

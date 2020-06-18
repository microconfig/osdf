package io.microconfig.osdf.service;

import io.cluster.old.cluster.resource.ClusterResource;
import io.cluster.old.cluster.resource.LocalClusterResource;

import java.util.List;

public interface ClusterService {
    String name();

    String version();

    List<ClusterResource> resources();

    void upload(List<LocalClusterResource> resources);

    void delete();
}

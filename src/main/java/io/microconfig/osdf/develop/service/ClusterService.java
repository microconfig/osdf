package io.microconfig.osdf.develop.service;

import io.microconfig.osdf.develop.cluster.resource.ClusterResource;
import io.microconfig.osdf.develop.cluster.resource.LocalClusterResource;

import java.util.List;

public interface ClusterService {
    String name();

    String version();

    List<ClusterResource> resources();

    void upload(List<LocalClusterResource> resources);
}

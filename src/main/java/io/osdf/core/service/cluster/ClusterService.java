package io.osdf.core.service.cluster;

import io.osdf.core.cluster.resource.ClusterResource;
import io.osdf.core.cluster.resource.LocalClusterResource;

import java.util.List;

public interface ClusterService {
    String name();

    String version();

    List<ClusterResource> resources();

    void upload(List<LocalClusterResource> resources);

    void delete();
}

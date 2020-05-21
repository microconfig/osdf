package io.microconfig.osdf.develop.component;

import io.microconfig.osdf.develop.cluster.resource.ClusterResource;
import io.microconfig.osdf.develop.cluster.resource.LocalClusterResource;

import java.util.List;

public interface ClusterComponent {
    List<ClusterResource> resources();

    void upload(List<LocalClusterResource> resources);
}

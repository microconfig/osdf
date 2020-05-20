package io.microconfig.osdf.develop.component;

import io.microconfig.osdf.develop.cluster.ClusterResource;
import io.microconfig.osdf.develop.cluster.LocalClusterResource;

import java.util.List;

public interface ClusterComponent {
    List<ClusterResource> resources();

    void upload(List<LocalClusterResource> resources);
}

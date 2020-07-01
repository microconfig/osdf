package io.osdf.core.cluster.deployment;

import io.osdf.core.cluster.pod.Pod;
import io.osdf.core.cluster.resource.ClusterResource;

import java.util.List;

public interface ClusterDeployment {
    String name();

    List<Pod> pods();

    void scale(int replicas);

    ClusterResource toResource();
}

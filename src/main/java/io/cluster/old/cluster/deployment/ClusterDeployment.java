package io.cluster.old.cluster.deployment;

import io.cluster.old.cluster.pod.Pod;
import io.cluster.old.cluster.resource.ClusterResource;

import java.util.List;

public interface ClusterDeployment {
    String name();

    List<Pod> pods();

    void scale(int replicas);

    ClusterResource toResource();
}

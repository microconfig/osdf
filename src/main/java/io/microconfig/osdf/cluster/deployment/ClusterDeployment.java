package io.microconfig.osdf.cluster.deployment;

import io.microconfig.osdf.cluster.pod.Pod;

import java.util.List;

public interface ClusterDeployment {
    String name();

    List<Pod> pods();

    void scale(int replicas);
}

package io.microconfig.osdf.develop.deployment;

import io.microconfig.osdf.openshift.Pod;

import java.util.List;

public interface ClusterDeployment {
    String name();

    List<Pod> pods();

    void scale(int replicas);
}

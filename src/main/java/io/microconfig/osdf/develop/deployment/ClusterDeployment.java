package io.microconfig.osdf.develop.deployment;

import io.microconfig.osdf.develop.deployment.info.ClusterDeploymentInfo;
import io.microconfig.osdf.openshift.Pod;

import java.nio.file.Path;
import java.util.List;

public interface ClusterDeployment {
    String name();

    String version();

    String serviceName();

    List<Pod> pods();

    void scale(int replicas);

    boolean createConfigMap(List<Path> configs);

    ClusterDeploymentInfo info();
}

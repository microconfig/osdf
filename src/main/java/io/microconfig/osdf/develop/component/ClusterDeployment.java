package io.microconfig.osdf.develop.component;

import io.microconfig.osdf.openshift.Pod;

import java.nio.file.Path;
import java.util.List;

public interface ClusterDeployment {
    String name();

    String version();

    List<Pod> pods();

    void restart();

    void stop();

    boolean createConfigMap(List<Path> configs);

    ClusterDeploymentInfo info();

    String label();
}

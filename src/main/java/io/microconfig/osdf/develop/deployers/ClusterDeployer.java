package io.microconfig.osdf.develop.deployers;

import io.microconfig.osdf.develop.component.ClusterComponent;
import io.microconfig.osdf.develop.component.ClusterDeployment;
import io.microconfig.osdf.develop.component.ComponentFiles;

public interface ClusterDeployer {
    void deploy(ClusterComponent component, ClusterDeployment deployment, ComponentFiles files);
}

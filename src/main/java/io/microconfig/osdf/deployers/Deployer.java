package io.microconfig.osdf.deployers;

import io.microconfig.osdf.components.DeploymentComponent;

public interface Deployer {
    void deploy(DeploymentComponent component);
}

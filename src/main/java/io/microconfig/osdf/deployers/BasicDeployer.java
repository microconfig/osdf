package io.microconfig.osdf.deployers;

import io.microconfig.osdf.components.DeploymentComponent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BasicDeployer implements Deployer {
    public static BasicDeployer basicDeployer() {
        return new BasicDeployer();
    }

    @Override
    public void deploy(DeploymentComponent component) {
        component.deleteOldResourcesFromOpenShift();
        component.createConfigMap();
        component.upload();
    }
}

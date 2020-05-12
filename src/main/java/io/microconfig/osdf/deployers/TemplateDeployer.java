package io.microconfig.osdf.deployers;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.components.TemplateComponent;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.deployers.BasicDeployer.basicDeployer;

@RequiredArgsConstructor
public class TemplateDeployer implements Deployer {
    public static TemplateDeployer templateDeployer() {
        return new TemplateDeployer();
    }

    @Override
    public void deploy(DeploymentComponent component) {
        if (component instanceof TemplateComponent) {
            ((TemplateComponent) component).uploadTemplate();
        } else {
            basicDeployer().deploy(component);
        }
    }
}

package io.microconfig.osdf.deployers;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.openshift.OpenShiftCLI;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.deployers.BasicDeployer.basicDeployer;
import static io.microconfig.osdf.istio.VirtualService.virtualService;

@RequiredArgsConstructor
public class HiddenDeployer implements Deployer {
    private final OpenShiftCLI oc;

    public static HiddenDeployer hiddenDeployer(OpenShiftCLI oc) {
        return new HiddenDeployer(oc);
    }

    @Override
    public void deploy(DeploymentComponent component) {
        if (component.isDeployed()) throw new OSDFException("Component already deployed");
        if (!virtualServiceExists(component)) throw new OSDFException("Virtual service doesn't exist");
        basicDeployer().deploy(component);
    }

    private boolean virtualServiceExists(DeploymentComponent component) {
        return virtualService(oc, component).exists();
    }
}

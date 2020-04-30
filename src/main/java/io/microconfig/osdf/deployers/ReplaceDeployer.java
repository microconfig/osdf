package io.microconfig.osdf.deployers;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.istio.VirtualService;
import io.microconfig.osdf.openshift.OCExecutor;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.deployers.BasicDeployer.basicDeployer;
import static io.microconfig.osdf.istio.VirtualService.virtualService;
import static io.microconfig.utils.Logger.info;

@RequiredArgsConstructor
public class ReplaceDeployer implements Deployer {
    private final OCExecutor oc;

    public static ReplaceDeployer replaceDeployer(OCExecutor oc) {
        return new ReplaceDeployer(oc);
    }

    @Override
    public void deploy(DeploymentComponent component) {
        info("Deploying " + component.getName());
        if (deployedOtherVersion(component)) {
            component.deleteAll();
        }
        uploadVirtualService(component);
        basicDeployer().deploy(component);
    }

    private boolean deployedOtherVersion(DeploymentComponent component) {
        List<DeploymentComponent> deployedComponents = component.getDeployedComponents();
        if (deployedComponents.isEmpty()) return false;

        return deployedComponents.stream()
                .noneMatch(deployed -> deployed.getVersion().equals(component.getVersion()));
    }

    public void uploadVirtualService(DeploymentComponent component) {
        if (!component.isIstioService()) return;
        VirtualService virtualService = virtualService(oc, component);
        virtualService.createEmpty();
        virtualService.upload();
    }
}

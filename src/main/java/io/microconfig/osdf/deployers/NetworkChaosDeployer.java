package io.microconfig.osdf.deployers;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.istio.VirtualService;
import io.microconfig.osdf.istio.faults.Fault;
import io.microconfig.osdf.openshift.OCExecutor;
import lombok.AllArgsConstructor;

import static io.microconfig.osdf.istio.VirtualService.virtualService;

@AllArgsConstructor
public class NetworkChaosDeployer implements Deployer {

    private final OCExecutor oc;
    private final Fault fault;

    public static NetworkChaosDeployer chaosDeployer(OCExecutor oc) {
        return new NetworkChaosDeployer(oc, null);
    }

    public static NetworkChaosDeployer chaosDeployer(OCExecutor oc, Fault fault) {
        return new NetworkChaosDeployer(oc, fault);
    }

    @Override
    public void deploy(DeploymentComponent component) {
        if (!component.isIstioService()) return;
        VirtualService virtualService = virtualService(oc, component);
        virtualService.setFault(fault).upload();
    }
}

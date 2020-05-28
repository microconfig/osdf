package io.microconfig.osdf.deployers;

import io.microconfig.osdf.istio.faults.Fault;
import io.microconfig.osdf.service.ClusterService;
import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.files.ServiceFiles;
import io.microconfig.osdf.service.istio.IstioService;
import lombok.AllArgsConstructor;

import static io.microconfig.osdf.utils.IstioUtils.toIstioService;
import static io.microconfig.utils.Logger.announce;


@AllArgsConstructor
public class NetworkChaosDeployer implements ServiceDeployer {

    private final Fault fault;

    public static NetworkChaosDeployer chaosDeployer(Fault fault) {
        return new NetworkChaosDeployer(fault);
    }

    @Override
    public void deploy(ClusterService service, ServiceDeployment deployment, ServiceFiles files) {
        IstioService istioService = toIstioService(service);
        istioService.virtualService().setFault(fault).upload();
        if (fault != null) {
            announce("Network fault injected into " + istioService.name());
        } else {
            announce("Network faults removed from " + istioService.name());
        }
    }
}

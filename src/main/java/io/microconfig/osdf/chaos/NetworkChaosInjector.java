package io.microconfig.osdf.chaos;

import io.microconfig.osdf.istio.Fault;
import io.microconfig.osdf.service.ClusterService;
import io.microconfig.osdf.service.istio.IstioService;
import lombok.AllArgsConstructor;

import static io.microconfig.osdf.service.istio.IstioService.toIstioService;


@AllArgsConstructor
public class NetworkChaosInjector {
    private final Fault fault;

    public static NetworkChaosInjector chaosInjector(Fault fault) {
        return new NetworkChaosInjector(fault);
    }

    public String inject(ClusterService service) {
        IstioService istioService = toIstioService(service);
        istioService.virtualService().setFault(fault).upload();
        if (fault != null) {
            return "Network fault injected into " + istioService.name();
        } else {
            return "Network faults removed from " + istioService.name();
        }
    }
}

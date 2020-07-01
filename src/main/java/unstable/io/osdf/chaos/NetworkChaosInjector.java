package unstable.io.osdf.chaos;

import unstable.io.osdf.istio.Fault;
import io.osdf.core.service.cluster.ClusterService;
import io.osdf.core.service.cluster.types.istio.IstioService;
import lombok.AllArgsConstructor;

import static io.osdf.core.service.cluster.types.istio.IstioService.toIstioService;


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

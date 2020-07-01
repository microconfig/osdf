package unstable.io.osdf.istio.rulesetters;

import io.osdf.core.service.core.deployment.types.istio.IstioServiceDeployment;
import io.osdf.core.service.cluster.types.istio.IstioService;

public interface RoutingRuleSetter {
    boolean set(IstioService service, IstioServiceDeployment deployment, String rule);
}

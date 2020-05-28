package io.microconfig.osdf.istio.rulesetters;

import io.microconfig.osdf.service.deployment.istio.IstioServiceDeployment;
import io.microconfig.osdf.service.istio.IstioService;

public interface RoutingRuleSetter {
    boolean set(IstioService service, IstioServiceDeployment deployment, String rule);
}

package io.microconfig.osdf.istio.rulesetters;

import io.microconfig.osdf.components.DeploymentComponent;

public interface RoutingRuleSetter {
    boolean set(DeploymentComponent component, String rule);
}

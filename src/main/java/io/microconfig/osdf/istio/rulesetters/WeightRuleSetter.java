package io.microconfig.osdf.istio.rulesetters;

import io.microconfig.osdf.deprecated.components.DeploymentComponent;
import io.microconfig.osdf.cluster.openshift.OpenShiftCLI;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.istio.VirtualService.virtualService;
import static io.microconfig.osdf.utils.StringUtils.castToInteger;

@RequiredArgsConstructor
public class WeightRuleSetter implements RoutingRuleSetter {
    private final OpenShiftCLI oc;

    public static WeightRuleSetter weightRule(OpenShiftCLI oc) {
        return new WeightRuleSetter(oc);
    }

    @Override
    public boolean set(DeploymentComponent component, String rule) {
        Integer weight = castToInteger(rule);
        if (weight == null) return false;

        virtualService(oc, component)
                .setWeight(weight)
                .upload();
        return true;
    }
}

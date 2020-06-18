package io.microconfig.osdf.istio.rulesetters;

import io.microconfig.osdf.service.deployment.istio.IstioServiceDeployment;
import io.microconfig.osdf.service.istio.IstioService;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.utils.StringUtils.castToInteger;

@RequiredArgsConstructor
public class WeightRuleSetter implements RoutingRuleSetter {
    public static WeightRuleSetter weightRule() {
        return new WeightRuleSetter();
    }

    @Override
    public boolean set(IstioService service, IstioServiceDeployment deployment, String rule) {
        Integer weight = castToInteger(rule);
        if (weight == null) return false;

        service.virtualService()
                .setWeight(deployment.encodedVersion(), weight)
                .upload();
        return true;
    }
}

package io.microconfig.osdf.istio.rulesetters;

import io.microconfig.osdf.service.deployment.istio.IstioServiceDeployment;
import io.microconfig.osdf.service.istio.IstioService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HeaderRuleSetter implements RoutingRuleSetter {
    public static HeaderRuleSetter headerRule() {
        return new HeaderRuleSetter();
    }

    @Override
    public boolean set(IstioService service, IstioServiceDeployment deployment, String rule) {
        if (!rule.equals("header")) return false;

        service.virtualService()
                .setHeader(deployment.encodedVersion())
                .upload();

        return true;
    }
}

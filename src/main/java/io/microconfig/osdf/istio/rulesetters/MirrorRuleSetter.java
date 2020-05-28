package io.microconfig.osdf.istio.rulesetters;

import io.microconfig.osdf.service.deployment.istio.IstioServiceDeployment;
import io.microconfig.osdf.service.istio.IstioService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MirrorRuleSetter implements RoutingRuleSetter {
    public static MirrorRuleSetter mirrorRule() {
        return new MirrorRuleSetter();
    }

    @Override
    public boolean set(IstioService service, IstioServiceDeployment deployment, String rule) {
        if (!rule.equals("mirror")) return false;
        service.virtualService()
                .setMirror(deployment.encodedVersion())
                .upload();
        return true;
    }
}

package unstable.io.osdf.istio.rulesetters;

import io.osdf.core.service.core.deployment.types.istio.IstioServiceDeployment;
import io.osdf.core.service.cluster.types.istio.IstioService;
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

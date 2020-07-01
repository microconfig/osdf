package unstable.io.osdf.istio.rulesetters;

import io.osdf.core.service.core.deployment.types.istio.IstioServiceDeployment;
import io.osdf.core.service.cluster.types.istio.IstioService;
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

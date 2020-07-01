package unstable.io.osdf.istio.rulesetters;

import io.osdf.core.service.core.deployment.types.istio.IstioServiceDeployment;
import io.osdf.core.service.cluster.types.istio.IstioService;
import lombok.RequiredArgsConstructor;

import static io.osdf.common.utils.StringUtils.castToInteger;

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

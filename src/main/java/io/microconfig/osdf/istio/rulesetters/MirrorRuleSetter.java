package io.microconfig.osdf.istio.rulesetters;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.openshift.OCExecutor;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.istio.VirtualService.virtualService;

@RequiredArgsConstructor
public class MirrorRuleSetter implements RoutingRuleSetter {
    private final OCExecutor oc;

    public static MirrorRuleSetter mirrorRule(OCExecutor oc) {
        return new MirrorRuleSetter(oc);
    }

    @Override
    public boolean set(DeploymentComponent component, String rule) {
        if (!rule.equals("mirror")) return false;
        virtualService(oc, component)
                .setMirror()
                .upload();
        return true;
    }
}

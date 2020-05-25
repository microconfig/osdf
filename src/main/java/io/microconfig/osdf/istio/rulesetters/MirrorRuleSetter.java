package io.microconfig.osdf.istio.rulesetters;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.openshift.OpenShiftCLI;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.istio.VirtualService.virtualService;

@RequiredArgsConstructor
public class MirrorRuleSetter implements RoutingRuleSetter {
    private final OpenShiftCLI oc;

    public static MirrorRuleSetter mirrorRule(OpenShiftCLI oc) {
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

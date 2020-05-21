package io.microconfig.osdf.istio.rulesetters;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.openshift.OpenShiftCLI;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.istio.VirtualService.virtualService;

@RequiredArgsConstructor
public class HeaderRuleSetter implements RoutingRuleSetter {
    private final OpenShiftCLI oc;

    public static HeaderRuleSetter headerRule(OpenShiftCLI oc) {
        return new HeaderRuleSetter(oc);
    }

    @Override
    public boolean set(DeploymentComponent component, String rule) {
        if (!rule.equals("header")) return false;

        virtualService(oc, component)
                .setHeader()
                .upload();

        return true;
    }
}

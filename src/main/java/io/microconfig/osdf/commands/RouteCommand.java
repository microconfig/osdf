package io.microconfig.osdf.commands;

import io.microconfig.osdf.deprecated.components.DeploymentComponent;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.istio.rulesetters.RoutingRuleSetter;
import io.microconfig.osdf.cluster.openshift.OpenShiftCLI;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.deprecated.components.DeploymentComponent.component;

@RequiredArgsConstructor
public class RouteCommand {
    private final OSDFPaths paths;
    private final OpenShiftCLI oc;
    private final List<RoutingRuleSetter> ruleSetters;

    public void set(String componentName, String rule) {
        DeploymentComponent component = component(componentName, paths, oc);
        boolean ruleIsSet = ruleSetters.stream().anyMatch(setter -> setter.set(component, rule));
        if (!ruleIsSet) throw new OSDFException("Unknown routing rule");
    }
}

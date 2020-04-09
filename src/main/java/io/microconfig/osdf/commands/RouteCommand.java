package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.istio.rulesetters.RoutingRuleSetter;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.OpenShiftProject;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.components.DeploymentComponent.component;
import static io.microconfig.osdf.openshift.OpenShiftProject.create;

@RequiredArgsConstructor
public class RouteCommand {
    private final OSDFPaths paths;
    private final OCExecutor oc;
    private final List<RoutingRuleSetter> ruleSetters;

    public void set(String componentName, String rule) {
        DeploymentComponent component = component(componentName, paths.componentsPath(), oc);
        try (OpenShiftProject ignored = create(paths, oc).connect()) {
            boolean ruleIsSet = ruleSetters.stream().anyMatch(setter -> setter.set(component, rule));
            if (!ruleIsSet) throw new RuntimeException("Unknown routing rule");
        }
    }
}

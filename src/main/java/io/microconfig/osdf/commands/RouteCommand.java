package io.microconfig.osdf.commands;

import io.cluster.old.cluster.cli.ClusterCLI;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.istio.rulesetters.RoutingRuleSetter;
import io.osdf.settings.paths.OSDFPaths;
import io.microconfig.osdf.service.deployment.istio.IstioServiceDeployment;
import io.microconfig.osdf.service.deployment.pack.ServiceDeployPack;
import io.microconfig.osdf.service.istio.IstioService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.service.deployment.pack.loader.DefaultServiceDeployPacksLoader.serviceLoader;

@RequiredArgsConstructor
public class RouteCommand {
    private final OSDFPaths paths;
    private final ClusterCLI cli;
    private final List<RoutingRuleSetter> ruleSetters;

    public void set(String serviceName, String rule) {
        ServiceDeployPack serviceDeployPack = serviceLoader(paths, cli).loadByName(serviceName);
        if (!(serviceDeployPack.service() instanceof IstioService)) throw new OSDFException(serviceName + " is not istio service");
        if (!(serviceDeployPack.deployment() instanceof IstioServiceDeployment)) throw new OSDFException(serviceName + " is not istio service");

        IstioService service = (IstioService) serviceDeployPack.service();
        IstioServiceDeployment deployment = (IstioServiceDeployment) serviceDeployPack.deployment();

        boolean ruleIsSet = ruleSetters.stream().anyMatch(setter -> setter.set(service, deployment, rule));
        if (!ruleIsSet) throw new OSDFException("Unknown routing rule");
    }
}

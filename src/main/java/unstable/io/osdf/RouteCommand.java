package unstable.io.osdf;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.common.exceptions.OSDFException;
import unstable.io.osdf.istio.rulesetters.RoutingRuleSetter;
import io.osdf.settings.paths.OsdfPaths;
import io.osdf.core.service.core.deployment.types.istio.IstioServiceDeployment;
import io.osdf.core.service.core.deployment.pack.ServiceDeployPack;
import io.osdf.core.service.cluster.types.istio.IstioService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.core.service.core.deployment.pack.loader.DefaultServiceDeployPacksLoader.serviceLoader;

@RequiredArgsConstructor
public class RouteCommand {
    private final OsdfPaths paths;
    private final ClusterCli cli;
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

package io.microconfig.osdf.service.matchers;

import io.cluster.old.cluster.cli.ClusterCli;
import io.microconfig.osdf.service.ClusterService;
import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.deployment.istio.IstioServiceDeployment;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.service.DefaultClusterService.defaultClusterService;
import static io.microconfig.osdf.service.istio.DefaultIstioService.istioService;

@RequiredArgsConstructor
public class ClusterServiceMatcher {
    private final ClusterCli cli;

    public static ClusterServiceMatcher matcher(ClusterCli cli) {
        return new ClusterServiceMatcher(cli);
    }

    public ClusterService match(ServiceDeployment deployment) {
        if (deployment instanceof IstioServiceDeployment) {
            IstioServiceDeployment istioDeployment = (IstioServiceDeployment) deployment;
            return istioService(istioDeployment.serviceName(), istioDeployment.version(), cli);
        }
        return defaultClusterService(deployment.serviceName(), deployment.version(), cli);
    }
}

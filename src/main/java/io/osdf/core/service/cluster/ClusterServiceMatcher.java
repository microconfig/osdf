package io.osdf.core.service.cluster;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.service.core.deployment.ServiceDeployment;
import io.osdf.core.service.core.deployment.types.istio.IstioServiceDeployment;
import lombok.RequiredArgsConstructor;

import static io.osdf.core.service.cluster.types.DefaultClusterService.defaultClusterService;
import static io.osdf.core.service.cluster.types.istio.DefaultIstioService.istioService;

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

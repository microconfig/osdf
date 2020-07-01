package io.osdf.actions.management.deploy.deployers;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import io.osdf.core.service.cluster.ClusterService;
import io.osdf.core.service.core.deployment.ServiceDeployment;
import io.osdf.core.service.local.ServiceFiles;
import lombok.RequiredArgsConstructor;

import static io.osdf.actions.management.deploy.deployers.BaseServiceDeployer.baseServiceDeployer;
import static io.osdf.actions.management.deploy.deployers.hooks.UploadVirtualServiceHook.uploadVirtualServiceHook;

@RequiredArgsConstructor
public class IstioServiceDeployer implements ServiceDeployer {
    private final OsdfPaths paths;
    private final ClusterCli cli;

    public static IstioServiceDeployer istioServiceDeployer(OsdfPaths paths, ClusterCli cli) {
        return new IstioServiceDeployer(paths, cli);
    }

    @Override
    public void deploy(ClusterService service, ServiceDeployment deployment, ServiceFiles files) {
        baseServiceDeployer(cli, uploadVirtualServiceHook()).deploy(service, deployment, files);
    }
}

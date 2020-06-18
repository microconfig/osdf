package io.osdf.management.deploy.deployers;

import io.cluster.old.cluster.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import io.microconfig.osdf.service.ClusterService;
import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.files.ServiceFiles;
import lombok.RequiredArgsConstructor;

import static io.osdf.management.deploy.deployers.BaseServiceDeployer.baseServiceDeployer;
import static io.osdf.management.deploy.deployers.hooks.UploadVirtualServiceHook.uploadVirtualServiceHook;

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

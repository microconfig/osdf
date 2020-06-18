package io.osdf.management.deploy.deployers;

import io.cluster.old.cluster.cli.ClusterCLI;
import io.osdf.settings.paths.OSDFPaths;
import io.microconfig.osdf.service.ClusterService;
import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.files.ServiceFiles;
import lombok.RequiredArgsConstructor;

import static io.osdf.management.deploy.deployers.BaseServiceDeployer.baseServiceDeployer;
import static io.osdf.management.deploy.deployers.hooks.UploadVirtualServiceHook.uploadVirtualServiceHook;

@RequiredArgsConstructor
public class IstioServiceDeployer implements ServiceDeployer {
    private final OSDFPaths paths;
    private final ClusterCLI cli;

    public static IstioServiceDeployer istioServiceDeployer(OSDFPaths paths, ClusterCLI cli) {
        return new IstioServiceDeployer(paths, cli);
    }

    @Override
    public void deploy(ClusterService service, ServiceDeployment deployment, ServiceFiles files) {
        baseServiceDeployer(cli, uploadVirtualServiceHook()).deploy(service, deployment, files);
    }
}

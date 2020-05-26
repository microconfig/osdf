package io.microconfig.osdf.service.deployment.pack;

import io.microconfig.osdf.service.ClusterService;
import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.files.ServiceFiles;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultServiceDeployPack implements ServiceDeployPack {
    private final ServiceFiles files;
    private final ServiceDeployment deployment;
    private final ClusterService service;

    public static DefaultServiceDeployPack serviceDeployPack(ServiceFiles files, ServiceDeployment deployment,
                                                             ClusterService service) {
        return new DefaultServiceDeployPack(files, deployment, service);
    }

    @Override
    public ServiceFiles files() {
        return files;
    }

    @Override
    public ServiceDeployment deployment() {
        return deployment;
    }

    @Override
    public ClusterService service() {
        return service;
    }
}

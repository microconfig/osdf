package io.osdf.core.service.cluster.types.istio;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.cluster.resource.ClusterResource;
import io.osdf.core.cluster.resource.LocalClusterResource;
import unstable.io.osdf.istio.VirtualService;
import io.osdf.core.service.cluster.types.DefaultClusterService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.core.service.cluster.types.DefaultClusterService.defaultClusterService;

@RequiredArgsConstructor
public class DefaultIstioService implements IstioService {
    private final ClusterCli cli;
    private final DefaultClusterService service;

    public static DefaultIstioService istioService(String name, String version, ClusterCli cli) {
        return new DefaultIstioService(cli, defaultClusterService(name, version, cli));
    }

    @Override
    public VirtualService virtualService() {
        return VirtualService.virtualService(cli, name());
    }

    @Override
    public String name() {
        return service.name();
    }

    @Override
    public String version() {
        return service.version();
    }

    @Override
    public List<ClusterResource> resources() {
        return service.resources();
    }

    @Override
    public void upload(List<LocalClusterResource> resources) {
        service.upload(resources);
    }

    @Override
    public void delete() {
        service.delete();
        virtualService().delete();
    }
}

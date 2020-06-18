package io.microconfig.osdf.service.istio;

import io.cluster.old.cluster.cli.ClusterCli;
import io.cluster.old.cluster.resource.ClusterResource;
import io.cluster.old.cluster.resource.LocalClusterResource;
import io.microconfig.osdf.istio.VirtualService;
import io.microconfig.osdf.service.DefaultClusterService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.service.DefaultClusterService.defaultClusterService;

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

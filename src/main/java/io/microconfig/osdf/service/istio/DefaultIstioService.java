package io.microconfig.osdf.service.istio;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.cluster.resource.ClusterResource;
import io.microconfig.osdf.cluster.resource.LocalClusterResource;
import io.microconfig.osdf.istio.VirtualService;
import io.microconfig.osdf.service.DefaultClusterService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.service.DefaultClusterService.defaultClusterService;

@RequiredArgsConstructor
public class DefaultIstioService implements IstioService {
    private final ClusterCLI cli;
    private final DefaultClusterService service;

    public static DefaultIstioService istioService(String name, String version, ClusterCLI cli) {
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

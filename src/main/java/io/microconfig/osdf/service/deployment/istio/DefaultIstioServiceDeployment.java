package io.microconfig.osdf.service.deployment.istio;

import io.cluster.old.cluster.cli.ClusterCLI;
import io.cluster.old.cluster.pod.Pod;
import io.cluster.old.cluster.resource.ClusterResource;
import io.microconfig.osdf.service.deployment.DefaultServiceDeployment;
import io.microconfig.osdf.service.deployment.info.ServiceDeploymentInfo;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

import static io.microconfig.osdf.service.deployment.DefaultServiceDeployment.defaultServiceDeployment;

@RequiredArgsConstructor
public class DefaultIstioServiceDeployment implements IstioServiceDeployment {
    private final DefaultServiceDeployment deployment;

    public static DefaultIstioServiceDeployment istioServiceDeployment(String name, String version, String serviceName,
                                                                       String resourceKind, ClusterCLI cli) {
        return new DefaultIstioServiceDeployment(defaultServiceDeployment(name, version, serviceName, resourceKind, cli));
    }

    @Override
    public String encodedVersion() {
        return version().toLowerCase().replace(".", "-d-");
    }

    @Override
    public boolean createConfigMap(List<Path> configs) {
        return deployment.createConfigMap(configs);
    }

    @Override
    public ServiceDeploymentInfo info() {
        return deployment.info();
    }

    @Override
    public String name() {
        return deployment.name();
    }

    @Override
    public List<Pod> pods() {
        return deployment.pods();
    }

    @Override
    public void scale(int replicas) {
        deployment.scale(replicas);
    }

    @Override
    public ClusterResource toResource() {
        return deployment.toResource();
    }

    @Override
    public String version() {
        return deployment.version();
    }

    @Override
    public String serviceName() {
        return deployment.serviceName();
    }
}

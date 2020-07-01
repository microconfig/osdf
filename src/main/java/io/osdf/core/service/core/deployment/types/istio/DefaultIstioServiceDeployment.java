package io.osdf.core.service.core.deployment.types.istio;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.cluster.pod.Pod;
import io.osdf.core.cluster.resource.ClusterResource;
import io.osdf.core.service.core.deployment.types.DefaultServiceDeployment;
import io.osdf.actions.info.info.deployment.ServiceDeploymentInfo;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

import static io.osdf.core.service.core.deployment.types.DefaultServiceDeployment.defaultServiceDeployment;

@RequiredArgsConstructor
public class DefaultIstioServiceDeployment implements IstioServiceDeployment {
    private final DefaultServiceDeployment deployment;

    public static DefaultIstioServiceDeployment istioServiceDeployment(String name, String version, String serviceName,
                                                                       String resourceKind, ClusterCli cli) {
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

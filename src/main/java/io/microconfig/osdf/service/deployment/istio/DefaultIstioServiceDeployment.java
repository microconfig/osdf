package io.microconfig.osdf.service.deployment.istio;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.cluster.pod.Pod;
import io.microconfig.osdf.service.deployment.DefaultServiceDeployment;
import io.microconfig.osdf.service.deployment.info.ServiceDeploymentInfo;
import io.microconfig.osdf.service.files.ServiceFiles;
import lombok.RequiredArgsConstructor;

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
    public boolean createConfigMap(ServiceFiles files) {
        return deployment.createConfigMap(files);
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
    public String version() {
        return deployment.version();
    }

    @Override
    public String serviceName() {
        return deployment.serviceName();
    }
}

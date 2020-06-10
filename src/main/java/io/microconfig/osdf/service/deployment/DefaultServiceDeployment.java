package io.microconfig.osdf.service.deployment;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.cluster.deployment.ClusterDeployment;
import io.microconfig.osdf.cluster.pod.Pod;
import io.microconfig.osdf.service.deployment.info.DefaultServiceDeploymentInfo;
import io.microconfig.osdf.service.deployment.info.ServiceDeploymentInfo;
import io.microconfig.osdf.service.files.ServiceFiles;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.cluster.configmap.DefaultConfigMapUploader.configMapUploader;
import static io.microconfig.osdf.cluster.deployment.DefaultClusterDeployment.defaultClusterDeployment;

@RequiredArgsConstructor
@Getter
public class DefaultServiceDeployment implements ServiceDeployment {
    private final String name;
    private final String version;
    private final String serviceName;
    private final String resourceKind;
    private final ClusterCLI cli;

    private final ClusterDeployment deployment;

    public static DefaultServiceDeployment defaultServiceDeployment(String name, String version, String serviceName,
                                                                    String resourceKind, ClusterCLI cli) {
        return new DefaultServiceDeployment(name, version, serviceName, resourceKind, cli, defaultClusterDeployment(name, resourceKind, cli));
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String version() {
        return version;
    }

    @Override
    public String serviceName() {
        return serviceName;
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
    public boolean createConfigMap(ServiceFiles files) {
        return configMapUploader(cli).upload(name, files.configs(), this);
    }

    @Override
    public ServiceDeploymentInfo info() {
        return DefaultServiceDeploymentInfo.deploymentInfo(name, resourceKind, cli);
    }
}

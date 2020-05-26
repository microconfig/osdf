package io.microconfig.osdf.develop.service.deployment;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.develop.cluster.deployment.ClusterDeployment;
import io.microconfig.osdf.develop.service.deployment.info.ServiceDeploymentInfo;
import io.microconfig.osdf.openshift.Pod;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

import static io.microconfig.osdf.develop.cluster.deployment.DefaultClusterDeployment.defaultClusterDeployment;
import static io.microconfig.osdf.develop.cluster.configmap.DefaultConfigMapUploader.configMapUploader;
import static io.microconfig.osdf.develop.service.deployment.info.DefaultServiceDeploymentInfo.deploymentInfo;

@RequiredArgsConstructor
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
    public boolean createConfigMap(List<Path> configs) {
        return configMapUploader(cli).upload(name, configs, this);
    }

    @Override
    public ServiceDeploymentInfo info() {
        return deploymentInfo(name, resourceKind, cli);
    }
}

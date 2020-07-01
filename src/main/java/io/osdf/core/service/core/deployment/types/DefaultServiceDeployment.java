package io.osdf.core.service.core.deployment.types;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.cluster.deployment.ClusterDeployment;
import io.osdf.core.cluster.resource.ClusterResource;
import io.osdf.actions.info.info.deployment.ServiceDeploymentInfo;
import io.osdf.core.cluster.pod.Pod;
import io.osdf.actions.info.info.deployment.DefaultServiceDeploymentInfo;
import io.osdf.core.service.core.deployment.ServiceDeployment;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

import static io.osdf.core.cluster.deployment.DefaultClusterDeployment.defaultClusterDeployment;
import static io.osdf.core.service.core.ConfigMapUploader.configMapUploader;

@RequiredArgsConstructor
public class DefaultServiceDeployment implements ServiceDeployment {
    private final String name;
    private final String version;
    private final String serviceName;
    private final String resourceKind;
    private final ClusterCli cli;

    private final ClusterDeployment deployment;

    public static DefaultServiceDeployment defaultServiceDeployment(String name, String version, String serviceName,
                                                                    String resourceKind, ClusterCli cli) {
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
    public ClusterResource toResource() {
        return deployment.toResource();
    }

    @Override
    public boolean createConfigMap(List<Path> configs) {
        return configMapUploader(cli).upload(name, configs, this);
    }

    @Override
    public ServiceDeploymentInfo info() {
        return DefaultServiceDeploymentInfo.deploymentInfo(name, resourceKind, cli);
    }
}

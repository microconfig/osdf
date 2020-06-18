package io.microconfig.osdf.service.deployment;

import io.cluster.old.cluster.cli.ClusterCli;
import io.cluster.old.cluster.deployment.ClusterDeployment;
import io.cluster.old.cluster.resource.ClusterResource;
import io.microconfig.osdf.service.deployment.info.ServiceDeploymentInfo;
import io.cluster.old.cluster.pod.Pod;
import io.microconfig.osdf.service.deployment.info.DefaultServiceDeploymentInfo;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

import static io.cluster.old.cluster.deployment.DefaultClusterDeployment.defaultClusterDeployment;
import static io.cluster.old.cluster.configmap.DefaultConfigMapUploader.configMapUploader;

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

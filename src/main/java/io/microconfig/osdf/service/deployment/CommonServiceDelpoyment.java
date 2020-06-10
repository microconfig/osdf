package io.microconfig.osdf.service.deployment;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.cluster.pod.Pod;
import io.microconfig.osdf.cluster.resource.LocalClusterResource;
import io.microconfig.osdf.service.deployment.info.ServiceDeploymentInfo;
import io.microconfig.osdf.service.files.ServiceFiles;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static io.microconfig.osdf.cluster.configmap.DefaultConfigMapUploader.configMapUploader;
import static io.microconfig.osdf.service.deployment.DefaultServiceDeployment.defaultServiceDeployment;
import static io.microconfig.osdf.utils.YamlUtils.getString;
import static io.microconfig.osdf.utils.YamlUtils.loadFromPath;

@RequiredArgsConstructor
public class CommonServiceDelpoyment implements ServiceDeployment {
    private final DefaultServiceDeployment defaultServiceDeployment;
    @Getter
    private final List<LocalClusterResource> resourcesNames;

    public static CommonServiceDelpoyment commonServiceDelpoyment(String name, String version, String serviceName,
                                                                  String resourceKind, ClusterCLI cli,
                                                                  List<LocalClusterResource> resourcesNames) {
        return new CommonServiceDelpoyment(
                defaultServiceDeployment(name, version, serviceName, resourceKind, cli),
                resourcesNames);
    }

    @Override
    public boolean createConfigMap(ServiceFiles files) {
        Map<String, Object> deploy = loadFromPath(files.getPath("service.deploy"));
        String configName = getString(deploy, "service", "config");
        return configMapUploader(defaultServiceDeployment.getCli())
                .upload(configName, files.configs(), defaultServiceDeployment);
    }

    @Override
    public ServiceDeploymentInfo info() {
        return defaultServiceDeployment.info();
    }

    @Override
    public String name() {
        return defaultServiceDeployment.name();
    }

    @Override
    public List<Pod> pods() {
        return defaultServiceDeployment.pods();
    }

    @Override
    public void scale(int replicas) {
        defaultServiceDeployment.scale(replicas);
    }

    @Override
    public String version() {
        return defaultServiceDeployment.version();
    }

    @Override
    public String serviceName() {
        return defaultServiceDeployment.serviceName();
    }
}

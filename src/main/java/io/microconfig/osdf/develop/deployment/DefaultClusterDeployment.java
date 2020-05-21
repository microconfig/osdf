package io.microconfig.osdf.develop.deployment;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.develop.deployment.info.ClusterDeploymentInfo;
import io.microconfig.osdf.openshift.Pod;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

import static io.microconfig.osdf.develop.deployment.configmap.DefaultConfigMapUploader.configMapUploader;
import static io.microconfig.osdf.develop.deployment.info.DefaultClusterDeploymentInfo.deploymentInfo;
import static io.microconfig.osdf.openshift.Pod.fromOpenShiftNotation;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class DefaultClusterDeployment implements ClusterDeployment {
    private final String name;
    private final String version;
    private final String serviceName;
    private final String resourceKind;
    private final ClusterCLI cli;

    public static DefaultClusterDeployment defaultClusterDeployment(String name, String version, String serviceName,
                                                                    String resourceKind, ClusterCLI cli) {
        return new DefaultClusterDeployment(name, version, serviceName, resourceKind, cli);
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
        return cli.execute("get pods " + label() + " -o name")
                .throwExceptionIfError()
                .getOutputLines()
                .stream()
                .filter(line -> line.length() > 0)
                .map(notation -> fromOpenShiftNotation(notation, name, cli))
                .sorted()
                .collect(toUnmodifiableList());
    }

    @Override
    public void scale(int replicas) {
        cli.execute("scale " + resourceKind + " " + name + " --replicas=" + replicas)
                .throwExceptionIfError();
    }

    @Override
    public boolean createConfigMap(List<Path> configs) {
        return configMapUploader(cli).upload(configs, this);
    }

    @Override
    public ClusterDeploymentInfo info() {
        return deploymentInfo(name, resourceKind, cli);
    }

    private String label() {
        return "-l \"application in (" + serviceName + "), projectVersion in (" + version +  ")\"";
    }
}

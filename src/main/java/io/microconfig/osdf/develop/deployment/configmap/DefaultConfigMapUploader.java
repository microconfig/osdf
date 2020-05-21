package io.microconfig.osdf.develop.deployment.configmap;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.develop.deployment.ClusterDeployment;
import io.microconfig.osdf.utils.FileUtils;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@RequiredArgsConstructor
public class DefaultConfigMapUploader {
    private final ClusterCLI cli;

    public static DefaultConfigMapUploader configMapUploader(ClusterCLI cli) {
        return new DefaultConfigMapUploader(cli);
    }

    public boolean upload(List<Path> configs, ClusterDeployment deployment) {
        String hash = computeHash(configs);
        String currentHash = currentRemoteHash(deployment.name());
        if (hash.equals(currentHash)) return false;

        replaceConfigMap(deployment, configs, hash);
        return true;
    }

    private void replaceConfigMap(ClusterDeployment deployment, List<Path> configMapFiles, String hash) {
        deleteConfigMap(deployment.name());
        uploadConfigMap(deployment.name(), configMapFiles);
        labelConfigMap(deployment, hash);
    }

    private void deleteConfigMap(String name) {
        cli.execute("oc delete configmap " + name);
    }

    private void uploadConfigMap(String name, List<Path> configMapFiles) {
        String command = "create configmap " + name;
        String fromFileOptions = configMapFiles.stream()
                .map(path -> " --from-file " + path)
                .collect(joining());
        cli.execute(command + fromFileOptions)
                .throwExceptionIfError();
    }

    private void labelConfigMap(ClusterDeployment deployment, String hash) {
        String labelCommand = "label configmap " + deployment.name() +
                " application=" + deployment.serviceName() +
                " projectVersion=" + deployment.version() +
                " configHash=" + hash;
        cli.execute(labelCommand)
                .throwExceptionIfError();
    }

    private String currentRemoteHash(String name) {
        List<String> output = cli.execute("get configmap " + name + " -o custom-columns=\"hash:.metadata.labels.configHash\"")
                .getOutputLines();
        if (output.get(0).toLowerCase().contains("not found")) return "noHashFound";
        return output.get(1).strip();
    }

    private String computeHash(List<Path> configMapFiles) {
        return md5Hex(configMapFiles.stream()
                        .sorted()
                        .map(FileUtils::readAll)
                        .collect(joining()));
    }
}

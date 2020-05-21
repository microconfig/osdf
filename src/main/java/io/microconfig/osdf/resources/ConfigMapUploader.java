package io.microconfig.osdf.resources;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.openshift.OpenShiftCLI;
import io.microconfig.osdf.utils.FileUtils;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.file.Files.list;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@RequiredArgsConstructor
public class ConfigMapUploader {
    private final DeploymentComponent component;
    private final OpenShiftCLI oc;

    public static ConfigMapUploader configMapUploader(DeploymentComponent component, OpenShiftCLI oc) {
        return new ConfigMapUploader(component, oc);
    }

    public boolean upload() {
        try (Stream<Path> files = list(component.getConfigDir())) {
            List<Path> configMapFiles = configMapFiles(files);
            String hash = computeHash(configMapFiles);
            String currentHash = currentRemoteHash();
            if (hash.equals(currentHash)) return false;

            replaceConfigMap(configMapFiles, hash);
            return true;
        } catch (IOException e) {
            throw new OSDFException("Can't read from " + component.getConfigDir(), e);
        }
    }

    public String localHash() {
        try (Stream<Path> files = list(component.getConfigDir())) {
            return computeHash(configMapFiles(files));
        } catch (IOException e) {
            throw new OSDFException("Can't read from " + component.getConfigDir(), e);
        }
    }

    private void replaceConfigMap(List<Path> configMapFiles, String hash) {
        deleteConfigMap();
        uploadConfigMap(configMapFiles);
        labelConfigMap(hash);
    }

    private void deleteConfigMap() {
        oc.execute("oc delete configmap " + component.fullName());
    }

    private void uploadConfigMap(List<Path> configMapFiles) {
        String command = "oc create configmap " + component.fullName();
        String fromFileOptions = configMapFiles.stream()
                .map(path -> " --from-file " + path)
                .collect(joining());
        oc.execute(command + fromFileOptions)
                .throwExceptionIfError();
    }

    private void labelConfigMap(String hash) {
        String labelCommand = "oc label configmap " + component.fullName() +
                " application=" + component.getName() +
                " projectVersion=" + component.getVersion() +
                " configHash=" + hash;
        oc.execute(labelCommand)
                .throwExceptionIfError();
    }

    private String currentRemoteHash() {
        List<String> output = oc.execute("oc get configmap " + component.fullName() + " -o custom-columns=\"hash:.metadata.labels.configHash\"").getOutputLines();
        if (output.get(0).toLowerCase().contains("not found")) return "noHashFound";
        return output.get(1).strip();
    }

    private String computeHash(List<Path> configMapFiles) {
        return md5Hex(configMapFiles.stream()
                        .sorted()
                        .map(FileUtils::readAll)
                        .collect(joining()));
    }

    private List<Path> configMapFiles(Stream<Path> files) {
        return files.filter(Files::isRegularFile)
                        .filter(file -> !file.getFileName().toString().equals("deploy.yaml"))
                        .filter(file -> !file.getFileName().toString().equals("process.properties"))
                        .filter(file -> !file.getFileName().toString().contains("diff-"))
                        .filter(file -> !file.getFileName().toString().contains("secret"))
                        .collect(toUnmodifiableList());
    }
}

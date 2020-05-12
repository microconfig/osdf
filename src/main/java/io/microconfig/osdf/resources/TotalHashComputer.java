package io.microconfig.osdf.resources;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.utils.FileUtils;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import static io.microconfig.osdf.resources.ConfigMapUploader.configMapUploader;
import static java.nio.file.Files.list;
import static java.nio.file.Path.of;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@RequiredArgsConstructor
public class TotalHashComputer {
    private final DeploymentComponent component;

    public static TotalHashComputer totalHashComputer(DeploymentComponent component) {
        return new TotalHashComputer(component);
    }

    public String computeHash() {
        String configsHash = configMapUploader(component, null).localHash();
        String resourcesHash = resourcesHash();
        return md5Hex(configsHash + resourcesHash);
    }

    private String resourcesHash() {
        Path resourcesDir = of(component.getConfigDir() + "/openshift");
        try (Stream<Path> resources = list(resourcesDir)) {
            return md5Hex(resources
                    .sorted()
                    .map(FileUtils::readAll)
                    .collect(joining()));
        } catch (IOException e) {
            throw new OSDFException("Can't read from " + resourcesDir, e);
        }
    }
}

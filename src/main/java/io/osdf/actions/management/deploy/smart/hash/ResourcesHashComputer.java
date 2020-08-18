package io.osdf.actions.management.deploy.smart.hash;

import io.osdf.common.utils.FileUtils;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.cluster.resource.LocalClusterResource;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.osdf.common.utils.FileUtils.readAll;
import static io.osdf.common.utils.FileUtils.writeStringToFile;
import static io.osdf.common.yaml.YamlObject.yaml;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@RequiredArgsConstructor
public class ResourcesHashComputer {
    private static final String HASH_PLACEHOLDER = "<CONFIG_HASH>";

    public static ResourcesHashComputer resourcesHashComputer() {
        return new ResourcesHashComputer();
    }

    public void insertIn(ApplicationFiles files) {
        if (files.metadata().getMainResource() == null) return;

        String currentHash = currentHash(files);
        if (currentHash == null || !currentHash.equals(HASH_PLACEHOLDER)) return;

        computeAndInsertHash(files);
    }

    public String currentHash(ApplicationFiles files) {
        String resourcePath = files.metadata()
                .getMainResource()
                .getPath();
        return yaml(Path.of(resourcePath)).get("metadata.labels.configHash");
    }

    private void computeAndInsertHash(ApplicationFiles files) {
        String hash = computeHash(files);
        insertHash(Path.of(files.metadata().getMainResource().getPath()), hash);
    }

    public String computeHash(ApplicationFiles files) {
        return md5Hex(files.resources().stream()
                .sorted()
                .map(LocalClusterResource::path)
                .map(FileUtils::readAll)
                .collect(joining())
        );
    }

    private void insertHash(Path path, String hash) {
        String newContent = readAll(path).replace(HASH_PLACEHOLDER, hash);
        writeStringToFile(path, newContent);
    }
}

package io.osdf.actions.management.deploy.smart.hash;

import io.osdf.common.utils.FileUtils;
import io.osdf.common.yaml.YamlObject;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.cluster.resource.LocalClusterResource;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

import static io.osdf.common.utils.FileUtils.readAll;
import static io.osdf.common.utils.FileUtils.writeStringToFile;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@RequiredArgsConstructor
public class ResourcesHashComputer {
    private static final String HASH_PLACEHOLDER = "<CONFIG_HASH>";

    public static ResourcesHashComputer resourcesHashComputer() {
        return new ResourcesHashComputer();
    }

    public void insertIn(ApplicationFiles files) {
        String currentHash = currentHash(files);
        if (currentHash == null || !currentHash.equals(HASH_PLACEHOLDER)) return;

        computeAndInsertHash(files);
    }

    public String currentHash(ApplicationFiles files) {
        return files.resources()
                .stream()
                .map(LocalClusterResource::path)
                .map(YamlObject::yaml)
                .map(yaml -> yaml.<String>get("metadata.labels.configHash"))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private void computeAndInsertHash(ApplicationFiles files) {
        String hash = computeHash(files);
        insertHash(files, hash);
    }

    public String computeHash(ApplicationFiles files) {
        return md5Hex(files.resources().stream()
                .sorted()
                .map(LocalClusterResource::path)
                .map(FileUtils::readAll)
                .collect(joining())
        );
    }

    private void insertHash(ApplicationFiles files, String hash) {
        files.resources().forEach(resource -> {
            String newContent = readAll(resource.path()).replace(HASH_PLACEHOLDER, hash);
            writeStringToFile(resource.path(), newContent);
        });
    }
}

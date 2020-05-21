package io.microconfig.osdf.develop.resources;

import io.microconfig.osdf.develop.service.ServiceFiles;
import io.microconfig.osdf.develop.cluster.resource.LocalClusterResource;
import io.microconfig.osdf.utils.FileUtils;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@RequiredArgsConstructor
public class TotalHashComputer {
    private final ServiceFiles files;

    public static TotalHashComputer totalHashComputer(ServiceFiles files) {
        return new TotalHashComputer(files);
    }

    public String compute() {
        String configsHash = computeHashOfFiles(configs());
        String resourcesHash = computeHashOfFiles(resources());
        return md5Hex(configsHash + resourcesHash);
    }

    private Stream<Path> configs() {
        return files.configs().stream()
                .sorted();
    }

    private Stream<Path> resources() {
        return files.resources().stream()
                .sorted()
                .map(LocalClusterResource::path);
    }

    private String computeHashOfFiles(Stream<Path> files) {
        return md5Hex(files
                .map(FileUtils::readAll)
                .collect(joining())
        );
    }
}

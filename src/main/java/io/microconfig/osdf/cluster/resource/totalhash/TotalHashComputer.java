package io.microconfig.osdf.cluster.resource.totalhash;

import io.microconfig.osdf.cluster.resource.LocalClusterResource;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.service.files.ServiceFiles;
import io.microconfig.osdf.utils.FileUtils;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.stream.Stream;

import static io.microconfig.osdf.service.deployment.checkers.image.LatestImageVersionGetter.latestImageVersionGetter;
import static io.microconfig.utils.Logger.info;
import static java.lang.System.getenv;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@RequiredArgsConstructor
public class TotalHashComputer {
    private final OSDFPaths paths;
    private final ServiceFiles files;

    public static TotalHashComputer totalHashComputer(OSDFPaths paths, ServiceFiles files) {
        return new TotalHashComputer(paths, files);
    }

    public String compute() {
        String configsHash = computeHashOfFiles(configs());
        String resourcesHash = computeHashOfFiles(resources());
        String imageHash = latestImageVersionGetter(files, paths).get();
        String totalHash = md5Hex(configsHash + resourcesHash + imageHash);
        if ("true".equals(getenv("OSDF_LOG_DEPLOY_HASHES"))) {
            info(files.name() + " hashes: total[" + totalHash + "] = " +
                    "configs[" + configsHash + "] + " +
                    "resources[" + resourcesHash + "] + " +
                    "image[" + imageHash + "]");
        }
        return totalHash;
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

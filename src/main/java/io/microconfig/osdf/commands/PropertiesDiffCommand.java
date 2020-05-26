package io.microconfig.osdf.commands;

import io.microconfig.osdf.develop.service.deployment.pack.ServiceDeployPack;
import io.microconfig.osdf.develop.service.files.ServiceFiles;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static io.microconfig.osdf.develop.service.deployment.pack.loader.DefaultServiceDeployPacksLoader.defaultServiceDeployPacksLoader;
import static io.microconfig.osdf.microconfig.files.DiffFilesCollector.collector;
import static io.microconfig.utils.Logger.announce;
import static io.microconfig.utils.Logger.info;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newInputStream;

@RequiredArgsConstructor
public class PropertiesDiffCommand {
    private final OSDFPaths paths;

    public void show(List<String> serviceNames) {
        defaultServiceDeployPacksLoader(paths, serviceNames, null)
                .loadPacks().stream()
                .map(ServiceDeployPack::files)
                .forEach(this::showDiff);
    }

    private void showDiff(ServiceFiles files) {
        collector(files.root())
                .collect()
                .forEach(this::printDiffFile);
    }

    private void printDiffFile(Path file) {
        announce(componentName(file));
        try {
            info(IOUtils.toString(newInputStream(file), UTF_8.name()));
        } catch (IOException e) {
            throw new OSDFException("Can't access file " + file, e);
        }
    }

    private String componentName(Path file) {
        String[] tokens = file.toString().split("/");
        return tokens[tokens.length - 2];
    }
}

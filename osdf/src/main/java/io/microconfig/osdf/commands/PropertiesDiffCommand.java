package io.microconfig.osdf.commands;

import io.microconfig.osdf.exceptions.OSDFException;
import io.osdf.settings.paths.OsdfPaths;
import io.microconfig.osdf.service.deployment.pack.ServiceDeployPack;
import io.microconfig.osdf.service.files.ServiceFiles;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static io.microconfig.osdf.microconfig.files.DiffFilesCollector.collector;
import static io.microconfig.osdf.service.deployment.pack.loader.DefaultServiceDeployPacksLoader.serviceLoader;
import static io.microconfig.osdf.service.loaders.filters.RequiredComponentsFilter.requiredComponentsFilter;
import static io.microconfig.utils.Logger.announce;
import static io.microconfig.utils.Logger.info;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newInputStream;

@RequiredArgsConstructor
public class PropertiesDiffCommand {
    private final OsdfPaths paths;

    public void show(List<String> serviceNames) {
        serviceLoader(paths, requiredComponentsFilter(serviceNames), null)
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

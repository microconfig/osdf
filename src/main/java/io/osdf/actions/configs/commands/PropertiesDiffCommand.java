package io.osdf.actions.configs.commands;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.core.Application;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static io.microconfig.utils.Logger.announce;
import static io.microconfig.utils.Logger.info;
import static io.osdf.core.application.core.AllApplications.all;
import static io.osdf.core.application.core.files.loaders.ApplicationFilesLoaderImpl.activeRequiredAppsLoader;
import static io.osdf.core.local.microconfig.state.DiffFilesCollector.collector;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newInputStream;

@RequiredArgsConstructor
public class PropertiesDiffCommand {
    private final OsdfPaths paths;

    public void show(List<String> serviceNames) {
        activeRequiredAppsLoader(paths, serviceNames)
                .load(all(null)).stream()
                .map(Application::files)
                .forEach(this::showDiff);
    }

    private void showDiff(ApplicationFiles files) {
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

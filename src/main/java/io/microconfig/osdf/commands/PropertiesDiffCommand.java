package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.AbstractOpenShiftComponent;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static io.microconfig.osdf.components.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.utils.Logger.announce;
import static io.microconfig.utils.Logger.info;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newInputStream;

@RequiredArgsConstructor
public class PropertiesDiffCommand {
    private final OSDFPaths paths;

    public void show(List<String> components) {
        componentsLoader(paths, components, null).load().forEach(this::showDiff);
    }

    private void showDiff(AbstractOpenShiftComponent component) {
        component.diffFiles().forEach(this::printDiffFile);
    }

    private void printDiffFile(Path file) {
        announce(file.toString());
        try {
            info(IOUtils.toString(newInputStream(file), UTF_8.name()));
        } catch (IOException e) {
            throw new OSDFException("Can't access file " + file, e);
        }
    }
}

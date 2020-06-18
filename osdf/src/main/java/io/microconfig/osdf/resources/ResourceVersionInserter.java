package io.microconfig.osdf.resources;

import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import static io.microconfig.osdf.utils.FileUtils.*;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.list;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class ResourceVersionInserter {
    private final Path resourcesPath;
    private final String version;

    public static ResourceVersionInserter resourceVersionInserter(Path configDir, String version) {
        return new ResourceVersionInserter(of(configDir + "/openshift"), version);
    }

    public void insert() {
        if (!exists(resourcesPath)) return;
        try (Stream<Path> resources = list(resourcesPath)) {
            resources.forEach(this::doInsert);
        } catch (IOException e) {
            throw new OSDFException("Can't read resources in " + resourcesPath);
        }
    }

    private void doInsert(Path path) {
        String replacement = version == null ? "" : "." + version;
        String newContent = readAll(path).replace("<VERSION>", replacement);
        writeStringToFile(path, newContent);
    }
}

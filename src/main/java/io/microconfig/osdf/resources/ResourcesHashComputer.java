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
public class ResourcesHashComputer {
    private final Path resourcesPath;

    public static ResourcesHashComputer resourcesHashComputer(Path configDir) {
        return new ResourcesHashComputer(of(configDir + "/openshift"));
    }

    public void computeAll() {
        if (!exists(resourcesPath)) return;
        try (Stream<Path> resources = list(resourcesPath)) {
            resources.forEach(this::compute);
        } catch (IOException e) {
            throw new OSDFException("Can't read resources in " + resourcesPath);
        }
    }

    private void compute(Path path) {
        String s = hashOfFile(path);
        String newContent = readAll(path).replace("<CONFIG_HASH>", s);
        writeStringToFile(path, newContent);
    }
}

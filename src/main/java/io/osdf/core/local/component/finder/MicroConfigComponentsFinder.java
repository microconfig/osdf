package io.osdf.core.local.component.finder;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.local.component.ComponentDir;
import io.osdf.core.local.component.MicroConfigComponentDir;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static io.osdf.core.local.component.MicroConfigComponentDir.componentDir;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.list;
import static java.nio.file.Path.of;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class MicroConfigComponentsFinder implements ComponentsFinder {
    private final Path componentsPath;

    public static MicroConfigComponentsFinder componentsFinder(Path componentsPath) {
        return new MicroConfigComponentsFinder(componentsPath);
    }

    @Override
    public List<ComponentDir> findAll() {
        try (Stream<Path> files = list(componentsPath)) {
            return files.filter(Files::isDirectory)
                    .map(MicroConfigComponentDir::componentDir)
                    .collect(toUnmodifiableList());
        } catch (IOException e) {
            throw new OSDFException("Can't access " + componentsPath);
        }
    }

    @Override
    public ComponentDir findByName(String name) {
        Path componentRoot = of(componentsPath + "/" + name);
        if (!exists(componentRoot)) throw new OSDFException("Component " + name + " not found");
        return componentDir(componentRoot);
    }
}

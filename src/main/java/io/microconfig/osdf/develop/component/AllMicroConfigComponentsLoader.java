package io.microconfig.osdf.develop.component;

import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.file.Files.list;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class AllMicroConfigComponentsLoader implements ComponentsLoader {
    private final Path componentsPath;

    public static AllMicroConfigComponentsLoader componentsLoader(Path componentsPath) {
        return new AllMicroConfigComponentsLoader(componentsPath);
    }

    @Override
    public List<ComponentDir> load() {
        try (Stream<Path> files = list(componentsPath)) {
            return files.filter(Files::isDirectory)
                    .map(MicroConfigComponentDir::componentDir)
                    .collect(toUnmodifiableList());
        } catch (IOException e) {
            throw new OSDFException("Can't access " + componentsPath);
        }
    }
}

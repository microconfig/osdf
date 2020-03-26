package io.microconfig.osdf.components.loader;

import io.microconfig.osdf.components.AbstractOpenShiftComponent;
import io.microconfig.osdf.openshift.OCExecutor;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static io.microconfig.osdf.components.AbstractOpenShiftComponent.*;
import static java.nio.file.Files.list;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class ComponentsLoaderImpl implements ComponentsLoader {
    private final Path dir;
    private final List<String> requiredComponentsNames;
    private final OCExecutor oc;

    public static ComponentsLoaderImpl componentsLoader(Path dir, List<String> requiredComponentsNames, OCExecutor oc) {
        return new ComponentsLoaderImpl(dir, requiredComponentsNames, oc);
    }

    @Override
    public List<AbstractOpenShiftComponent> load() {
        return load(AbstractOpenShiftComponent.class);
    }

    @Override
    public <T extends AbstractOpenShiftComponent> List<T> load(Class<T> clazz) {
        return getPathsInDir()
                .filter(Files::isDirectory)
                .filter(path -> isComponentDir(path, requiredComponentsNames))
                .map(dir -> fromPath(dir, oc))
                .filter(Objects::nonNull)
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(toList());
    }

    private boolean isComponentDir(Path path, List<String> required) {
        String strPath = path.getFileName().toString();
        boolean isRequired = required == null || required.isEmpty() || required.contains(strPath);
        return !strPath.startsWith(".") && !strPath.equals("openshift") && isRequired;
    }

    private Stream<Path> getPathsInDir() {
        try {
            return list(dir);
        } catch (IOException e) {
            throw new UncheckedIOException("Couldn't open dir at " + dir, e);
        }
    }
}

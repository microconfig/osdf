package io.microconfig.osdf.components.loader;

import io.microconfig.osdf.components.AbstractOpenShiftComponent;
import io.microconfig.osdf.openshift.OCExecutor;
import lombok.RequiredArgsConstructor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static io.microconfig.osdf.components.AbstractOpenShiftComponent.fromPath;
import static io.microconfig.osdf.components.loader.ComponentDeployProperties.deployProperties;
import static io.microconfig.osdf.utils.FileUtils.getPathsInDir;
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
        return components()
                .filter(Objects::nonNull)
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(toList());
    }

    private Stream<AbstractOpenShiftComponent> components() {
        if (requiredComponentsNames != null && requiredComponentsNames.size() > 0) {
            return requiredComponentsNames
                    .stream()
                    .map(this::nameToComponent);
        } else {
            return getPathsInDir(dir)
                    .filter(Files::isDirectory)
                    .filter(this::isComponentDir)
                    .map(dir -> fromPath(dir, componentVersion(dir), oc));
        }
    }

    private AbstractOpenShiftComponent nameToComponent(String name) {
        if (name.contains("{")) {
            String[] split = name.split("([{}])");
            return fromPath(componentPath(split[0]), split[1], oc);
        } else {
            Path componentPath = componentPath(name);
            return fromPath(componentPath, componentVersion(componentPath), oc);
        }
    }

    private Path componentPath(String componentName) {
        return Path.of(dir + "/" + componentName);
    }

    private String componentVersion(Path dir) {
        ComponentDeployProperties properties = deployProperties(dir);
        String version = properties.getOrNull("version");
        if (version != null) return version;
        return properties.get("image", "version");
    }

    private boolean isComponentDir(Path path) {
        String strPath = path.getFileName().toString();
        return !strPath.startsWith(".") && !strPath.equals("openshift");
    }
}

package io.microconfig.osdf.components.loader;

import io.microconfig.osdf.components.AbstractOpenShiftComponent;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static io.microconfig.osdf.components.AbstractOpenShiftComponent.fromPath;
import static io.microconfig.osdf.components.properties.DeployProperties.deployProperties;
import static io.microconfig.osdf.groups.ActiveComponents.activeComponents;
import static java.nio.file.Files.exists;
import static java.nio.file.Path.of;
import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class ComponentsLoaderImpl implements ComponentsLoader {
    private final OSDFPaths paths;
    private final List<String> requiredComponentsNames;
    private final OCExecutor oc;

    public static ComponentsLoaderImpl componentsLoader(OSDFPaths paths, List<String> requiredComponentsNames, OCExecutor oc) {
        return new ComponentsLoaderImpl(paths, requiredComponentsNames, oc);
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
                .collect(toUnmodifiableList());
    }

    private Stream<AbstractOpenShiftComponent> components() {
        return activeComponents(paths)
                .get()
                .stream()
                .filter(this::isRequired)
                .filter(this::hasOpenShift)
                .map(this::nameToComponent);
    }

    private boolean isRequired(String component) {
        if (requiredComponentsNames != null && requiredComponentsNames.size() > 0) {
            return requiredComponentsNames.contains(component);
        }
        return true;
    }

    private AbstractOpenShiftComponent nameToComponent(String name) {
        if (name.contains("{")) {
            String[] split = name.split("([{}])");
            return fromPath(componentPath(split[0]), split[1], oc);
        }
        Path componentPath = componentPath(name);
        return fromPath(componentPath, deployProperties(componentPath).getVersion(), oc);
    }

    private Path componentPath(String componentName) {
        return of(paths.componentsPath() + "/" + componentName);
    }

    private boolean hasOpenShift(String component) {
        return exists(get(paths.componentsPath().toString(), component, "openshift"));
    }
}

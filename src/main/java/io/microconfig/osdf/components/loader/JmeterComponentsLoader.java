package io.microconfig.osdf.components.loader;

import io.microconfig.osdf.components.AbstractOpenShiftComponent;
import io.microconfig.osdf.components.ComponentType;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.microconfig.osdf.components.properties.DeployProperties.deployProperties;
import static io.microconfig.osdf.utils.FileUtils.getPathsInDir;
import static java.nio.file.Files.exists;
import static java.nio.file.Path.of;
import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.toUnmodifiableList;

@Setter
@RequiredArgsConstructor
public class JmeterComponentsLoader implements ComponentsLoader {

    private final OSDFPaths paths;
    private final OCExecutor oc;

    public static JmeterComponentsLoader componentsLoader(OSDFPaths paths, OCExecutor oc) {
        return new JmeterComponentsLoader(paths, oc);
    }

    @Override
    public List<AbstractOpenShiftComponent> load() {
        return load(AbstractOpenShiftComponent.class);
    }

    @Override
    public <T extends AbstractOpenShiftComponent> List<T> load(Class<T> clazz) {
        return loadComponents(allComponents(), clazz);
    }

    public List<String> getNamesOfAllJmeterComponents() {
        return allComponents()
                .map(AbstractOpenShiftComponent::getName)
                .collect(Collectors.toList());
    }

    public <T extends AbstractOpenShiftComponent> List<T> loadSpecificJmeterComponents(Class<T> clazz, boolean isSlaveNode) {
        Stream<AbstractOpenShiftComponent> components = specificJmeterComponent(isSlaveNode);
            return loadComponents(components, clazz);
        }

    public <T extends AbstractOpenShiftComponent> List<T> loadComponents(Stream<AbstractOpenShiftComponent> componentsStream,
                                                                         Class<T> clazz) {
        return componentsStream.filter(Objects::nonNull)
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(toUnmodifiableList());
    }

    private Stream<AbstractOpenShiftComponent> specificJmeterComponent(boolean isSlaveNode) {
        Stream<Path> componentPathStream = getComponentPath(isSlaveNode);
        return pathsToComponents(componentPathStream);
    }

    private Stream<AbstractOpenShiftComponent> allComponents() {
        Stream<Path> allPaths = Stream.concat(getComponentPath(true), getComponentPath(false));
        return pathsToComponents(allPaths);
    }

    private Stream<AbstractOpenShiftComponent> pathsToComponents(Stream<Path> paths) {
        return paths.map(path -> path.getFileName().toString())
                .filter(this::hasOpenShift)
                .map(this::nameToComponent);
    }

    private AbstractOpenShiftComponent nameToComponent(String componentName) {
        Path componentPath = of(paths.componentsPath() + "/" + componentName);
        return ComponentType.TEMPLATE.component(componentName, deployProperties(componentPath).getVersion(), componentPath, oc);
    }

    private boolean hasOpenShift(String component) {
        return exists(get(paths.componentsPath().toString(), component, "openshift"));
    }

    private Stream<Path> getComponentPath(boolean isSlaveNode) {
        return getPathsInDir(paths.componentsPath())
                .filter(path -> isSlaveNode ?
                        path.getFileName().toString().contains("slave") :
                        path.getFileName().toString().contains("master")
                )
                .filter(Files::isDirectory);
    }
}

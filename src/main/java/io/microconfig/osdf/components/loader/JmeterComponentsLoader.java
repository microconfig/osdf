package io.microconfig.osdf.components.loader;

import io.microconfig.osdf.components.AbstractOpenShiftComponent;
import io.microconfig.osdf.components.ComponentType;
import io.microconfig.osdf.loadtesting.jmeter.configs.JmeterConfigProcessor;
import io.microconfig.osdf.openshift.OCExecutor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
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

    private final JmeterConfigProcessor jmeterConfigProcessor;
    private final OCExecutor oc;

    public static JmeterComponentsLoader componentsLoader(JmeterConfigProcessor jmeterConfigProcessor, OCExecutor oc) {
        return new JmeterComponentsLoader(jmeterConfigProcessor, oc);
    }

    @Override
    public List<AbstractOpenShiftComponent> load() {
        return load(AbstractOpenShiftComponent.class);
    }

    @Override
    public <T extends AbstractOpenShiftComponent> List<T> load(Class<T> clazz) {
        return loadComponents(clazz, allComponents());
    }

    public <T extends AbstractOpenShiftComponent> T loadMaster(Class<T> clazz) {
        Stream<AbstractOpenShiftComponent> components = specificJmeterComponent(false);
        return loadComponents(clazz, components).stream()
                .findFirst()
                .orElseThrow();
    }

    public <T extends AbstractOpenShiftComponent> List<T> loadSlaves(Class<T> clazz) {
        Stream<AbstractOpenShiftComponent> components = specificJmeterComponent(true);
        return loadComponents(clazz, components);
    }

    public <T> List<T> loadComponents(Class<T> clazz, Stream<AbstractOpenShiftComponent> componentsStream) {
        return componentsStream.filter(Objects::nonNull)
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(toUnmodifiableList());
    }

    private Stream<AbstractOpenShiftComponent> specificJmeterComponent(boolean isSlaveNode) {
        Stream<Path> componentPathStream = getComponentPaths(isSlaveNode);
        return pathsToComponents(componentPathStream);
    }

    private Stream<AbstractOpenShiftComponent> allComponents() {
        Stream<Path> allPaths = Stream.concat(getComponentPaths(true), getComponentPaths(false));
        return pathsToComponents(allPaths);
    }

    private Stream<AbstractOpenShiftComponent> pathsToComponents(Stream<Path> paths) {
        return paths.map(path -> path.getFileName().toString())
                .filter(this::hasOpenShift)
                .map(this::nameToComponent);
    }

    private AbstractOpenShiftComponent nameToComponent(String componentName) {
        Path componentPath = of(jmeterConfigProcessor.getJmeterComponentsPath() + "/" + componentName);
        return ComponentType.DEPLOYMENT.component(componentName, deployProperties(componentPath).getVersion(), componentPath, oc);
    }

    private boolean hasOpenShift(String component) {
        return exists(get(jmeterConfigProcessor.getJmeterComponentsPath().toString(), component, "openshift"));
    }

    private Stream<Path> getComponentPaths(boolean isSlaveNode) {
        return getPathsInDir(jmeterConfigProcessor.getJmeterComponentsPath())
                .filter(path -> isSlaveNode == isSlaveDir(path))
                .filter(Files::isDirectory);
    }

    private boolean isSlaveDir(Path path) {
        return jmeterConfigProcessor.getSlaveConfigs()
                .stream()
                .anyMatch(config -> config.getName().equals(path.getFileName().toString()));
    }
}

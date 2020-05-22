package io.microconfig.osdf.components.loader;

import io.microconfig.osdf.components.JmeterComponent;
import io.microconfig.osdf.loadtesting.jmeter.configs.JmeterConfigProcessor;
import io.microconfig.osdf.openshift.OpenShiftCLI;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.microconfig.osdf.components.JmeterComponent.jmeterComponent;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.list;
import static java.nio.file.Path.of;
import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.toUnmodifiableList;

@Setter
@RequiredArgsConstructor
public class JmeterComponentsLoader {

    private final JmeterConfigProcessor jmeterConfigProcessor;
    private final OpenShiftCLI oc;

    public static JmeterComponentsLoader componentsLoader(JmeterConfigProcessor jmeterConfigProcessor, OpenShiftCLI oc) {
        return new JmeterComponentsLoader(jmeterConfigProcessor, oc);
    }

    public  List<JmeterComponent> load() {
        return loadComponents(allComponents());
    }

    public JmeterComponent loadMaster() {
        Stream<JmeterComponent> components = specificJmeterComponent(false);
        return loadComponents(components).stream()
                .findFirst()
                .orElseThrow();
    }

    public List<JmeterComponent> loadSlaves() {
        Stream<JmeterComponent> components = specificJmeterComponent(true);
        return loadComponents(components);
    }

    public List<JmeterComponent> loadComponents(Stream<JmeterComponent> componentsStream) {
        return componentsStream.filter(Objects::nonNull)
                .collect(toUnmodifiableList());
    }

    private Stream<JmeterComponent> specificJmeterComponent(boolean isSlaveNode) {
        Stream<Path> componentPathStream = getComponentPaths(isSlaveNode).stream();
        return pathsToComponents(componentPathStream);
    }

    private Stream<JmeterComponent> allComponents() {
        Stream<Path> allPaths = Stream.concat(getComponentPaths(true).stream(),
                getComponentPaths(false).stream());
        return pathsToComponents(allPaths);
    }

    private Stream<JmeterComponent> pathsToComponents(Stream<Path> paths) {
        return paths.map(path -> path.getFileName().toString())
                .filter(this::hasOpenShift)
                .map(this::nameToComponent);
    }

    private JmeterComponent nameToComponent(String componentName) {
        Path componentPath = of(jmeterConfigProcessor.getJmeterComponentsPath() + "/" + componentName);
        return jmeterComponent(componentName, componentPath, oc);
    }

    private boolean hasOpenShift(String component) {
        return exists(get(jmeterConfigProcessor.getJmeterComponentsPath().toString(), component, "openshift"));
    }

    private List<Path> getComponentPaths(boolean isSlaveNode) {
        Path componentsPath = jmeterConfigProcessor.getJmeterComponentsPath();
        try (Stream<Path> list = list(componentsPath)) {
            return list.filter(path -> isSlaveNode == isSlaveDir(path))
                    .filter(Files::isDirectory)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException("Couldn't open dir at " + componentsPath, e);
        }
    }

    private boolean isSlaveDir(Path path) {
        return jmeterConfigProcessor.getSlaveConfigs()
                .stream()
                .anyMatch(config -> config.getName().equals(path.getFileName().toString()));
    }
}

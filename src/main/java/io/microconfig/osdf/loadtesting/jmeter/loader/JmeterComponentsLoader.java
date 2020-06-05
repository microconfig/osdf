package io.microconfig.osdf.loadtesting.jmeter.loader;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.loadtesting.jmeter.JmeterComponent;
import io.microconfig.osdf.loadtesting.jmeter.configs.JmeterConfigProcessor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.microconfig.osdf.loadtesting.jmeter.JmeterComponent.jmeterComponent;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.list;
import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.toUnmodifiableList;

@Setter
@RequiredArgsConstructor
public class JmeterComponentsLoader {

    private final ClusterCLI cli;
    private final JmeterConfigProcessor jmeterConfigProcessor;

    public static JmeterComponentsLoader jmeterComponentsLoader(ClusterCLI cli, JmeterConfigProcessor jmeterConfigProcessor) {
        return new JmeterComponentsLoader(cli, jmeterConfigProcessor);
    }

    public List<JmeterComponent> load() {
        Stream<Path> allPaths = Stream.concat(getComponentPaths(true).stream(),
                getComponentPaths(false).stream());
        return loadComponents(pathsToComponents(allPaths));
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

    private Stream<JmeterComponent> pathsToComponents(Stream<Path> paths) {
        return paths.map(path -> path.getFileName().toString())
                .filter(this::hasOpenShift)
                .map(componentName -> {
                    Path componentPath = Paths.get(jmeterConfigProcessor.getJmeterComponentsPath().toString(), componentName);
                    return jmeterComponent(componentName, componentPath, cli);
                });
    }

    private boolean hasOpenShift(String component) {
        return exists(get(jmeterConfigProcessor.getJmeterComponentsPath().toString(), component, "resources"));
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

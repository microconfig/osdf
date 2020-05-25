package io.microconfig.osdf.loadtesting.jmeter;

import io.microconfig.osdf.components.JmeterComponent;
import io.microconfig.osdf.components.loader.JmeterComponentsLoader;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

import static io.microconfig.osdf.utils.FileUtils.deleteDirectory;
import static io.microconfig.osdf.utils.FileUtils.deleteFile;
import static io.microconfig.utils.Logger.announce;
import static java.nio.file.Path.of;
import static java.util.stream.IntStream.range;

@RequiredArgsConstructor
public class JmeterResourcesCleaner implements AutoCloseable {
    private final JmeterComponentsLoader componentsLoader;
    private final Path componentsPath;

    public static JmeterResourcesCleaner jmeterResourcesCleaner(JmeterComponentsLoader componentsLoader, Path componentsPath) {
        return new JmeterResourcesCleaner(componentsLoader, componentsPath);
    }

    public void cleanResources() {
        announce("Start cleaning resources");
        List<JmeterComponent> components = componentsLoader.load();
        int percent = 100 / components.size();
        range(0, components.size())
                .forEach(i -> {
                    JmeterComponent component = components.get(i);
                    announce("Cleaning ............................. " + (percent * (i + 1)) + "%");
                    component.deleteAll();
                    deleteDirectory(component.getComponentPath());
                });
        deleteFile(of(componentsPath + "/config/jmetertest.jmx"));
        announce("Clean has been finished");
    }

    @Override
    public void close() {
        cleanResources();
    }
}

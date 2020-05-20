package io.microconfig.osdf.loadtesting.jmeter;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.components.loader.JmeterComponentsLoader;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.IntStream;

import static io.microconfig.osdf.utils.FileUtils.deleteDirectory;
import static io.microconfig.utils.Logger.announce;

@RequiredArgsConstructor
public class JmeterResourcesCleaner implements AutoCloseable {
    private final JmeterComponentsLoader componentsLoader;

    public static JmeterResourcesCleaner jmeterResourcesCleaner(JmeterComponentsLoader componentsLoader) {
        return new JmeterResourcesCleaner(componentsLoader);
    }

    public void cleanResources() {
        announce("Start cleaning resources");
        List<DeploymentComponent> components = componentsLoader.load(DeploymentComponent.class);
        int percent = 100 / components.size();
        IntStream.range(0, components.size())
                .forEach(i -> {
                    DeploymentComponent component = components.get(i);
                    announce("Cleaning ............................. " + (percent * (i + 1)) + "%");
                    component.deleteAll();
                    deleteDirectory(component.getConfigDir());
                });
        announce("Clean has been finished");
    }

    @Override
    public void close() {
        cleanResources();
    }
}

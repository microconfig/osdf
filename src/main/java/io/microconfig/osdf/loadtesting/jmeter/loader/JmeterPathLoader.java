package io.microconfig.osdf.loadtesting.jmeter.loader;

import io.microconfig.osdf.component.ComponentDir;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.microconfig.osdf.component.finder.MicroConfigComponentsFinder.componentsFinder;
import static io.microconfig.osdf.component.loader.ComponentsLoaderImpl.componentsLoader;

@RequiredArgsConstructor
public class JmeterPathLoader {
    private final OSDFPaths paths;
    private final String componentName;

    public static JmeterPathLoader pathLoader(OSDFPaths paths, String componentName) {
        return new JmeterPathLoader(paths, componentName);
    }

    public Path jmeterComponentsPathLoad() {
        return componentsLoader().loadOne(componentName, componentsFinder(paths.componentsPath()), this::mapper);
    }

    private Path mapper(ComponentDir componentDir) {
        return componentDir.root();
    }
}

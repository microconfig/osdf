package io.microconfig.osdf.loadtesting.jmeter.loader;

import io.microconfig.osdf.component.ComponentDir;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.microconfig.osdf.component.finder.MicroConfigComponentsFinder.componentsFinder;
import static io.microconfig.osdf.component.loader.ComponentsLoaderImpl.componentsLoader;
import static java.nio.file.Files.exists;

@RequiredArgsConstructor
public class JmeterPathLoader {
    private final OSDFPaths paths;
    private final String markerFile;

    public static JmeterPathLoader jmeterPathLoader(OSDFPaths paths) {
        return new JmeterPathLoader(paths, "loadtest-mark.yaml");
    }

    public Path jmeterComponentsPathLoad() {
        return componentsLoader()
                .load(componentsFinder(paths.componentsPath()), this::check, this::mapper)
                .stream()
                .findFirst()
                .orElseThrow();
    }

    private boolean check(ComponentDir componentDir) {
        return exists(componentDir.getPath(markerFile));
    }

    private Path mapper(ComponentDir componentDir) {
        return componentDir.root();
    }
}

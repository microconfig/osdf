package io.microconfig.osdf.chaos.loader;

import io.microconfig.osdf.chaos.components.ChaosComponent;
import io.microconfig.osdf.chaos.components.DefaultChaosComponent;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.component.finder.MicroConfigComponentsFinder.componentsFinder;
import static io.microconfig.osdf.component.loader.ComponentsLoaderImpl.componentsLoader;

@RequiredArgsConstructor
public class ChaosComponentLoader {
    private final OSDFPaths paths;

    public static ChaosComponentLoader chaosComponentLoader(OSDFPaths paths) {
        return new ChaosComponentLoader(paths);
    }

    public ChaosComponent loadByName(String name) {
        return componentsLoader().loadOne(name, componentsFinder(paths.componentsPath()), DefaultChaosComponent::chaosFiles);
    }
}

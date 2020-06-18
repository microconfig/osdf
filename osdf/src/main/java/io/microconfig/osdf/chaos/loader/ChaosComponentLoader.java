package io.microconfig.osdf.chaos.loader;

import io.microconfig.osdf.chaos.components.ChaosComponent;
import io.microconfig.osdf.chaos.components.DefaultChaosComponent;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.component.finder.MicroConfigComponentsFinder.componentsFinder;
import static io.microconfig.osdf.component.loader.ComponentsLoaderImpl.componentsLoader;

@RequiredArgsConstructor
public class ChaosComponentLoader {
    private final OsdfPaths paths;

    public static ChaosComponentLoader chaosComponentLoader(OsdfPaths paths) {
        return new ChaosComponentLoader(paths);
    }

    public ChaosComponent loadByName(String name) {
        return componentsLoader().loadOne(name, componentsFinder(paths.componentsPath()), DefaultChaosComponent::chaosFiles);
    }
}

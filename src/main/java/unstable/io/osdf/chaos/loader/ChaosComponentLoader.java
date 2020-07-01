package unstable.io.osdf.chaos.loader;

import unstable.io.osdf.chaos.components.ChaosComponent;
import unstable.io.osdf.chaos.components.DefaultChaosComponent;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import static io.osdf.core.local.component.finder.MicroConfigComponentsFinder.componentsFinder;
import static io.osdf.core.local.component.loader.ComponentsLoaderImpl.componentsLoader;

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

package io.microconfig.osdf.service.loaders.filters;

import io.microconfig.osdf.component.ComponentDir;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

import static io.microconfig.osdf.configs.MicroConfigComponents.microConfigComponents;

@RequiredArgsConstructor
public class ActiveComponentsFilter implements Predicate<ComponentDir> {
    private final OSDFPaths paths;

    public static ActiveComponentsFilter activeComponentsFilter(OSDFPaths paths) {
        return new ActiveComponentsFilter(paths);
    }

    @Override
    public boolean test(ComponentDir componentDir) {
        return microConfigComponents(paths).active().contains(componentDir.name());
    }
}

package io.osdf.core.service.local.loaders.filters;

import io.osdf.core.local.component.ComponentDir;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

import static io.osdf.core.local.component.MicroConfigComponents.microConfigComponents;

@RequiredArgsConstructor
public class GroupComponentsFilter implements Predicate<ComponentDir> {
    private final OsdfPaths paths;
    private final String group;

    public static GroupComponentsFilter groupComponentsFilter(OsdfPaths paths, String group) {
        return new GroupComponentsFilter(paths, group);
    }

    @Override
    public boolean test(ComponentDir componentDir) {
        return microConfigComponents(paths).forGroup(group).contains(componentDir.name());
    }
}

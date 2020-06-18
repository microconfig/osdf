package io.microconfig.osdf.service.loaders.filters;

import io.microconfig.osdf.component.ComponentDir;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class RequiredComponentsFilter implements Predicate<ComponentDir> {
    private final List<String> requiredComponents;

    public static RequiredComponentsFilter requiredComponentsFilter(List<String> requiredComponents) {
        return new RequiredComponentsFilter(requiredComponents);
    }

    @Override
    public boolean test(ComponentDir componentDir) {
        if (requiredComponents == null) return true;
        return requiredComponents.contains(componentDir.name());
    }
}

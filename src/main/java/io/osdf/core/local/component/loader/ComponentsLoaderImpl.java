package io.osdf.core.local.component.loader;

import io.osdf.core.local.component.finder.ComponentsFinder;
import io.osdf.common.exceptions.OSDFException;

import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;

public class ComponentsLoaderImpl implements ComponentsLoader {
    public static ComponentsLoader componentsLoader() {
        return new ComponentsLoaderImpl();
    }

    @Override
    public <T> List<T> load(ComponentsFinder finder, ComponentTypeChecker checker, ComponentMapper<? extends T> mapper) {
        return finder.findAll()
                .stream()
                .filter(checker::check)
                .map(mapper::map)
                .collect(toUnmodifiableList());
    }

    @Override
    public <T> T loadOne(String name, ComponentsFinder finder, ComponentMapper<? extends T> mapper) {
        return finder.findAll().stream()
                .filter(component -> component.name().equals(name))
                .map(mapper::map)
                .findFirst()
                .orElseThrow(() -> new OSDFException("No component " + name + "found"));
    }
}

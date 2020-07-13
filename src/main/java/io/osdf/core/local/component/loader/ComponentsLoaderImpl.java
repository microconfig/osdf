package io.osdf.core.local.component.loader;

import io.osdf.core.local.component.ComponentDir;
import io.osdf.core.local.component.finder.ComponentsFinder;
import io.osdf.common.exceptions.OSDFException;

import java.util.List;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toUnmodifiableList;

public class ComponentsLoaderImpl implements ComponentsLoader {
    public static ComponentsLoader componentsLoader() {
        return new ComponentsLoaderImpl();
    }

    @Override
    public List<ComponentDir> load(ComponentsFinder finder, Predicate<ComponentDir> checker) {
        return finder.findAll()
                .stream()
                .filter(checker)
                .collect(toUnmodifiableList());
    }

    @Override
    public ComponentDir loadOne(String name, ComponentsFinder finder) {
        return finder.findAll().stream()
                .filter(component -> component.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new OSDFException("No component " + name + "found"));
    }
}

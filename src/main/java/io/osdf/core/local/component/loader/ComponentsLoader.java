package io.osdf.core.local.component.loader;

import io.osdf.core.local.component.finder.ComponentsFinder;

import java.util.List;

public interface ComponentsLoader {
    <T> List<T> load(ComponentsFinder finder, ComponentTypeChecker checker, ComponentMapper<? extends T> mapper);

    <T> T loadOne(String name, ComponentsFinder finder, ComponentMapper<? extends T> mapper);
}

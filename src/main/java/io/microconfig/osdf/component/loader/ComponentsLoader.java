package io.microconfig.osdf.component.loader;

import io.microconfig.osdf.component.finder.ComponentsFinder;

import java.util.List;

public interface ComponentsLoader {
    <T> List<T> load(ComponentsFinder finder, ComponentTypeChecker<? super T> checker, ComponentMapper<? extends T> mapper);

    <T> T loadOne(String name, ComponentsFinder finder, ComponentMapper<? extends T> mapper);
}

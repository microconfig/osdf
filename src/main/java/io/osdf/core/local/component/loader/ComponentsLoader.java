package io.osdf.core.local.component.loader;

import io.osdf.core.local.component.ComponentDir;
import io.osdf.core.local.component.finder.ComponentsFinder;

import java.util.List;
import java.util.function.Predicate;

public interface ComponentsLoader {
   List<ComponentDir> load(ComponentsFinder finder, Predicate<ComponentDir> checker);

    ComponentDir loadOne(String name, ComponentsFinder finder);
}

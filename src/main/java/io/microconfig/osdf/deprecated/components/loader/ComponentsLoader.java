package io.microconfig.osdf.deprecated.components.loader;

import io.microconfig.osdf.deprecated.components.AbstractOpenShiftComponent;

import java.util.List;

public interface ComponentsLoader {
    List<AbstractOpenShiftComponent> load();

    <T extends AbstractOpenShiftComponent> List<T> load(Class<T> clazz);
}

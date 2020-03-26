package io.microconfig.osdf.components.loader;

import io.microconfig.osdf.components.AbstractOpenShiftComponent;

import java.util.List;

public interface ComponentsLoader {
    List<AbstractOpenShiftComponent> load();

    <T extends AbstractOpenShiftComponent> List<T> load(Class<T> clazz);
}

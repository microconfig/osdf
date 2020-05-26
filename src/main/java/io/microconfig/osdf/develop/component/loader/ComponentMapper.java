package io.microconfig.osdf.develop.component.loader;

import io.microconfig.osdf.develop.component.ComponentDir;

public interface ComponentMapper<T> {
    T map(ComponentDir componentDir);
}

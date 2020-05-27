package io.microconfig.osdf.component.loader;

import io.microconfig.osdf.component.ComponentDir;

public interface ComponentMapper<T> {
    T map(ComponentDir componentDir);
}

package io.osdf.core.local.component.loader;

import io.osdf.core.local.component.ComponentDir;

public interface ComponentMapper<T> {
    T map(ComponentDir componentDir);
}

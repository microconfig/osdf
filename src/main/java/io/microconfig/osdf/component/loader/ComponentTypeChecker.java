package io.microconfig.osdf.component.loader;

import io.microconfig.osdf.component.ComponentDir;

public interface ComponentTypeChecker<T> {
    boolean check(ComponentDir componentDir);
}

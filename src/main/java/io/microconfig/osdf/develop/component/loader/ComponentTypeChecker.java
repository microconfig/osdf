package io.microconfig.osdf.develop.component.loader;

import io.microconfig.osdf.develop.component.ComponentDir;

public interface ComponentTypeChecker<T> {
    boolean check(ComponentDir componentDir);
}

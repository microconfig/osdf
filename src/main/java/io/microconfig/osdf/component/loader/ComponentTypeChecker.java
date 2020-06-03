package io.microconfig.osdf.component.loader;

import io.microconfig.osdf.component.ComponentDir;

public interface ComponentTypeChecker {
    boolean check(ComponentDir componentDir);
}

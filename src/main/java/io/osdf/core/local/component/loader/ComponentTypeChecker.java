package io.osdf.core.local.component.loader;

import io.osdf.core.local.component.ComponentDir;

public interface ComponentTypeChecker {
    boolean check(ComponentDir componentDir);
}

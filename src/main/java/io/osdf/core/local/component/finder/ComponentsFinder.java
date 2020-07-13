package io.osdf.core.local.component.finder;

import io.osdf.core.local.component.ComponentDir;

import java.util.List;

public interface ComponentsFinder {
    List<ComponentDir> findAll();

    ComponentDir findByName(String name);
}

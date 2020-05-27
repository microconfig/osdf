package io.microconfig.osdf.component.finder;

import io.microconfig.osdf.component.ComponentDir;

import java.util.List;

public interface ComponentsFinder {
    List<ComponentDir> findAll();

    ComponentDir findByName(String name);
}

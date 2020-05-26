package io.microconfig.osdf.develop.component.finder;

import io.microconfig.osdf.develop.component.ComponentDir;

import java.util.List;

public interface ComponentsFinder {
    List<ComponentDir> findAll();

    ComponentDir findByName(String name);
}

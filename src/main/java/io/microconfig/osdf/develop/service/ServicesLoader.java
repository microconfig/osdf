package io.microconfig.osdf.develop.service;

import io.microconfig.osdf.develop.component.ComponentDir;

import java.util.List;

public interface ServicesLoader {
    List<ServiceFiles> load(List<? extends ComponentDir> componentDirs);
}

package io.microconfig.osdf.develop.service.loaders;

import io.microconfig.osdf.develop.component.ComponentDir;
import io.microconfig.osdf.develop.service.files.ServiceFiles;

import java.util.List;

public interface ServicesLoader {
    List<ServiceFiles> load(List<? extends ComponentDir> componentDirs);
}

package io.microconfig.osdf.develop.service.loaders;

import io.microconfig.osdf.develop.service.files.ServiceFiles;

import java.util.List;

public interface ServiceFilesLoader {
    List<ServiceFiles> load();
}

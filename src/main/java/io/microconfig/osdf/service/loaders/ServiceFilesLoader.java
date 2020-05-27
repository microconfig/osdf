package io.microconfig.osdf.service.loaders;

import io.microconfig.osdf.service.files.ServiceFiles;

import java.util.List;

public interface ServiceFilesLoader {
    List<ServiceFiles> load();
}

package io.osdf.core.service.local.loaders;

import io.osdf.core.service.local.ServiceFiles;

import java.util.List;

public interface ServiceFilesLoader {
    List<ServiceFiles> load();
}

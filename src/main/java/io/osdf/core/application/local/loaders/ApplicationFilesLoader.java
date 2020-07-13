package io.osdf.core.application.local.loaders;

import io.osdf.core.application.local.ApplicationFiles;

import java.util.List;

public interface ApplicationFilesLoader {
    List<ApplicationFiles> load();

    <T> List<T> load(ApplicationMapper<T> appMapper);
}

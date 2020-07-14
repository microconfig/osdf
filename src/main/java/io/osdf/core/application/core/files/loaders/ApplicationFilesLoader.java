package io.osdf.core.application.core.files.loaders;

import io.osdf.core.application.core.files.ApplicationFiles;

import java.util.List;

public interface ApplicationFilesLoader {
    List<ApplicationFiles> load();

    <T> List<T> load(ApplicationMapper<T> appMapper);
}

package io.osdf.core.application.core.files.loaders;

import io.osdf.core.application.core.files.ApplicationFiles;

public interface ApplicationMapper<T> {
    boolean check(ApplicationFiles files);

    T map(ApplicationFiles files);
}

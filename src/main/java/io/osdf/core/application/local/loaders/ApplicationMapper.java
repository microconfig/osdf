package io.osdf.core.application.local.loaders;

import io.osdf.core.application.local.ApplicationFiles;

public interface ApplicationMapper<T> {
    boolean check(ApplicationFiles files);

    T map(ApplicationFiles files);
}

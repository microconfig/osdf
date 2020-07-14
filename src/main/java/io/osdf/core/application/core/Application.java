package io.osdf.core.application.core;

import io.osdf.core.application.core.description.CoreDescription;
import io.osdf.core.application.core.files.ApplicationFiles;

public interface Application {
    String name();

    boolean exists();

    void delete();

    ApplicationFiles files();

    CoreDescription coreDescription();
}

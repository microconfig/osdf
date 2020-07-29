package io.osdf.core.application.core;

import io.osdf.core.application.core.description.CoreDescription;
import io.osdf.core.application.core.files.ApplicationFiles;

import java.util.Optional;

public interface Application {
    String name();

    boolean exists();

    void delete();

    ApplicationFiles files();

    Optional<CoreDescription> coreDescription();
}

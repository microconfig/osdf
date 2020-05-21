package io.microconfig.osdf.develop.component;

import java.nio.file.Path;

public interface ComponentDir {
    String name();

    Path root();

    Path getPath(String identifier);
}

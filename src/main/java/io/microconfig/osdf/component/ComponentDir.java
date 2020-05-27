package io.microconfig.osdf.component;

import java.nio.file.Path;

public interface ComponentDir {
    String name();

    Path root();

    Path getPath(String identifier);
}

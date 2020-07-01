package io.osdf.core.local.component;

import java.nio.file.Path;

public interface ComponentDir {
    String name();

    Path root();

    Path getPath(String identifier);
}

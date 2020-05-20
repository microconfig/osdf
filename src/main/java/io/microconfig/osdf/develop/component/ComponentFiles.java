package io.microconfig.osdf.develop.component;

import io.microconfig.osdf.develop.cluster.LocalClusterResource;

import java.nio.file.Path;
import java.util.List;

public interface ComponentFiles {
    Path getPath(String name);

    List<LocalClusterResource> resources();

    List<Path> configs();
}

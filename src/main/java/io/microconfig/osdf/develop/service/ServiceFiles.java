package io.microconfig.osdf.develop.service;

import io.microconfig.osdf.develop.cluster.resource.LocalClusterResource;
import io.microconfig.osdf.develop.component.ComponentDir;

import java.nio.file.Path;
import java.util.List;

public interface ServiceFiles extends ComponentDir {
    List<LocalClusterResource> resources();

    List<Path> configs();
}

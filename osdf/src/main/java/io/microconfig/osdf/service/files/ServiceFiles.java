package io.microconfig.osdf.service.files;

import io.cluster.old.cluster.resource.LocalClusterResource;
import io.microconfig.osdf.component.ComponentDir;

import java.nio.file.Path;
import java.util.List;

public interface ServiceFiles extends ComponentDir {
    List<LocalClusterResource> resources();

    List<Path> configs();
}

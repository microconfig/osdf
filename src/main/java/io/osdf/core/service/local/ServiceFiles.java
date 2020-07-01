package io.osdf.core.service.local;

import io.osdf.core.cluster.resource.LocalClusterResource;
import io.osdf.core.local.component.ComponentDir;

import java.nio.file.Path;
import java.util.List;

public interface ServiceFiles extends ComponentDir {
    List<LocalClusterResource> resources();

    List<Path> configs();
}

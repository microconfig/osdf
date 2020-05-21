package io.microconfig.osdf.develop.cluster.resource;

import java.nio.file.Path;

public interface LocalClusterResource extends ClusterResource {
    Path path();
}

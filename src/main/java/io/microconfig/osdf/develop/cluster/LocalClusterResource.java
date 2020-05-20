package io.microconfig.osdf.develop.cluster;

import java.nio.file.Path;

public interface LocalClusterResource extends ClusterResource {
    Path path();
}

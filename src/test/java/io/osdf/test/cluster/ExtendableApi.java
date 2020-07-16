package io.osdf.test.cluster;

import io.osdf.core.connection.cli.ClusterCli;

public interface ExtendableApi {
    default void add(ClusterCli cliApi) {
        throw new RuntimeException("Is not actually extendable");
    }
}

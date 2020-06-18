package io.cluster.old.cluster.cli;

import io.cluster.old.cluster.commandline.CommandLineOutput;

public interface ClusterCLI {
    CommandLineOutput execute(String command);

    void login();
}

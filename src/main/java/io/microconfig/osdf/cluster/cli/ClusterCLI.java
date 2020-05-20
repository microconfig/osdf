package io.microconfig.osdf.cluster.cli;

import io.microconfig.osdf.commandline.CommandLineOutput;

public interface ClusterCLI {
    CommandLineOutput execute(String command);
}


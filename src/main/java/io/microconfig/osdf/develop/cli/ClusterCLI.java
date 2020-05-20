package io.microconfig.osdf.develop.cli;

import io.microconfig.osdf.commandline.CommandLineOutput;

public interface ClusterCLI {
    CommandLineOutput execute(String command);
}

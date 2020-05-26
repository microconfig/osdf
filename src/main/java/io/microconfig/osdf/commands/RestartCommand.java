package io.microconfig.osdf.commands;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RestartCommand {
    private final OSDFPaths paths;
    private final ClusterCLI cli;

    public void run(List<String> serviceNames) {

    }
}

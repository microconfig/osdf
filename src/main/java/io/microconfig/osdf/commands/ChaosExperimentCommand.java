package io.microconfig.osdf.commands;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.paths.OSDFPaths;

import static io.microconfig.osdf.chaos.ChaosTest.chaosTest;

public class ChaosExperimentCommand {
    private final OSDFPaths paths;
    private final ClusterCLI cli;

    public ChaosExperimentCommand(OSDFPaths paths, ClusterCLI cli) {
        this.paths = paths;
        this.cli = cli;
    }

    public void run() {
        chaosTest(paths, cli).run();
    }
}

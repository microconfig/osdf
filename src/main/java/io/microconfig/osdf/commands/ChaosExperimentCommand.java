package io.microconfig.osdf.commands;

import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.paths.OSDFPaths;

import static io.microconfig.osdf.chaos.ChaosTest.chaosTest;

public class ChaosExperimentCommand {
    private final OSDFPaths paths;
    private final OCExecutor ocExecutor;

    public ChaosExperimentCommand(OSDFPaths paths, OCExecutor ocExecutor) {
        this.paths = paths;
        this.ocExecutor = ocExecutor;
    }

    public void run() {
        chaosTest(paths, ocExecutor).run();
    }
}

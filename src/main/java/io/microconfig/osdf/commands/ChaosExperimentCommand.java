package io.microconfig.osdf.commands;

import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.paths.OSDFPaths;

import java.nio.file.Path;

import static io.microconfig.osdf.chaos.ChaosTest.chaosTest;

public class ChaosExperimentCommand {
    private final OSDFPaths paths;
    private final OCExecutor ocExecutor;
    private final Path pathToPlan;

    public ChaosExperimentCommand(OSDFPaths paths, OCExecutor ocExecutor, Path pathToPlan) {
        this.paths = paths;
        this.ocExecutor = ocExecutor;
        this.pathToPlan = pathToPlan;

    }

    public void run() {
        chaosTest(paths, ocExecutor, pathToPlan).run();
    }
}

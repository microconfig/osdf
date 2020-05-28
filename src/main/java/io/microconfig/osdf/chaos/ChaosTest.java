package io.microconfig.osdf.chaos;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChaosTest {
    final private ChaosTestPlan testPlan;
    final private OSDFPaths paths;
    final private ClusterCLI cli;

    public static ChaosTest chaosTest(OSDFPaths paths, ClusterCLI cli) {
        return new ChaosTest(
                ChaosTestPlan.fromYaml(paths.chaosPlanPath()),
                paths,
                cli
        );
    }

    public void run() {
        checkSteps();
        testPlan.steps().forEach(chaosStep -> chaosStep.runStep(paths, cli));
    }

    private void checkSteps() {
        testPlan.steps().forEach(ChaosStep::checkValues);
    }

}

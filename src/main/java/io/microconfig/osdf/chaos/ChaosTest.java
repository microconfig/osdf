package io.microconfig.osdf.chaos;

import io.microconfig.osdf.commands.NetworkChaosCommand;
import io.microconfig.osdf.commands.StopIoStressCommand;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.AllArgsConstructor;

import java.nio.file.Path;

import static io.microconfig.osdf.deployers.NetworkChaosDeployer.chaosDeployer;

@AllArgsConstructor
public class ChaosTest {
    final private ChaosTestPlan testPlan;
    final private OSDFPaths paths;
    final private OCExecutor ocExecutor;

    public static ChaosTest chaosTest(OSDFPaths paths, OCExecutor ocExecutor, Path pathToPlan) {
        return new ChaosTest(
                ChaosTestPlan.fromYaml(pathToPlan),
                paths,
                ocExecutor
        );
    }

    public void run() {
        checkSteps();
        testPlan.steps().forEach(chaosStep -> chaosStep.runStep(paths, ocExecutor));
        stopAllChaos();
    }

    private void stopAllChaos() {
        new NetworkChaosCommand(paths, ocExecutor, chaosDeployer(ocExecutor, null)).run(null);
        new StopIoStressCommand(paths, ocExecutor).run(null);
    }

    private void checkSteps() {
        testPlan.steps().forEach(ChaosStep::checkValues);
    }

}

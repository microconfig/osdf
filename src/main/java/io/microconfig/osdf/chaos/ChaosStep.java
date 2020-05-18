package io.microconfig.osdf.chaos;

import io.microconfig.osdf.commands.KillPodsCommand;
import io.microconfig.osdf.commands.NetworkChaosCommand;
import io.microconfig.osdf.commands.StartIoStressCommand;
import io.microconfig.osdf.deployers.NetworkChaosDeployer;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static io.microconfig.osdf.chaos.ChaosSet.chaosSet;
import static io.microconfig.osdf.utils.ThreadUtils.sleepSec;
import static io.microconfig.osdf.utils.YamlUtils.*;

@AllArgsConstructor
public class ChaosStep {
    private final Integer number;
    private final List<String> components;
    private final Integer severity;
    private final Integer duration;
    private final ChaosSet chaosSet;

    @SuppressWarnings("unchecked")
    public static ChaosStep fromMap(Object step) {
        Map<String, Object> stepMap = (Map<String, Object>) step;
        List<Object> components = getList(stepMap, "components");
        return new ChaosStep(
                getInt(stepMap, "number"),
                (List<String>) (Object) components,
                getInt(stepMap, "severity"),
                getInt(stepMap, "duration"),
                chaosSet(
                        getInt(getMap(stepMap, "chaosSet"), "abort"),
                        getInt(getMap(stepMap, "chaosSet"), "delay"),
                        getInt(getMap(stepMap, "chaosSet"), "ioChaos"),
                        getInt(getMap(stepMap, "chaosSet"), "podChaos")
                )
        );
    }

    public void runStep(OSDFPaths paths, OCExecutor ocExecutor) {
        long start = System.nanoTime();

        runAbort(paths, ocExecutor);
        runDelay(paths, ocExecutor);
        runIO(paths, ocExecutor);
        runPod(paths, ocExecutor, start);

        long current = System.nanoTime();

        //wait if finished earlier
        sleepSec(duration - TimeUnit.NANOSECONDS.toSeconds(current - start));

        //If network faults were injected at this step, remove them
        stopNetworkChaos(paths, ocExecutor);
    }

    private void stopNetworkChaos(OSDFPaths paths, OCExecutor ocExecutor) {
        if (chaosSet.isHttpDelay() || chaosSet.isHttpError()) {
            new NetworkChaosCommand(paths, ocExecutor, new NetworkChaosDeployer(ocExecutor, null)).run(components);
        }
    }

    private void runPod(OSDFPaths paths, OCExecutor ocExecutor, long start) {
        if (chaosSet.isKillPod()) {
            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleAtFixedRate(() -> new KillPodsCommand(paths, ocExecutor, severity).run(components), 0, chaosSet.getIoStressTimeout(), TimeUnit.SECONDS);
            long current = System.nanoTime();
            sleepSec(duration - TimeUnit.NANOSECONDS.toSeconds(current - start));
            service.shutdownNow();
        }
    }

    private void runIO(OSDFPaths paths, OCExecutor ocExecutor) {
        if (chaosSet.isIOStress()) {
            new StartIoStressCommand(paths, ocExecutor, chaosSet.getIoStressTimeout(), severity).run(components);
        }
    }

    private void runDelay(OSDFPaths paths, OCExecutor ocExecutor) {
        if (chaosSet.isHttpDelay()) {
            new NetworkChaosCommand(
                    paths,
                    ocExecutor,
                    new NetworkChaosDeployer(ocExecutor, chaosSet.getHttpDelayFault(severity))
            ).run(components);
        }
    }

    private void runAbort(OSDFPaths paths, OCExecutor ocExecutor) {
        if (chaosSet.isHttpError()) {
            new NetworkChaosCommand(
                    paths,
                    ocExecutor,
                    new NetworkChaosDeployer(ocExecutor, chaosSet.getHttpErrorFault(severity))
            ).run(components);
        }
    }

    public void checkValues() {
        if (number == null) {
            throw new RuntimeException("Assign numbers for all steps in test plan!");
        }

        if (severity == null) {
            throw new RuntimeException("Assign severity for step " + number);
        }

        if (severity < 0 || severity > 100) {
            throw new RuntimeException("Incorrect severity " + severity + " in step " + number);
        }

        if (duration == null) {
            throw new RuntimeException("Assign duration for step " + number);
        }

        if (duration < 0) {
            throw new RuntimeException("Incorrect duration " + duration + " in step " + number);
        }
    }
}

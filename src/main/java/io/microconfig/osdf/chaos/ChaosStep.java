package io.microconfig.osdf.chaos;

import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.microconfig.osdf.chaos.ChaosSet.chaosSet;
import static io.microconfig.osdf.utils.ThreadUtils.sleepSec;
import static io.microconfig.osdf.utils.YamlUtils.*;
import static io.microconfig.utils.Logger.announce;
import static java.lang.System.nanoTime;

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
        Map<String, Object> chaosSetMap = getMap(stepMap, "chaosSet");
        return new ChaosStep(
                getInt(stepMap, "number"),
                (List<String>) (Object) components,
                getInt(stepMap, "severity"),
                getInt(stepMap, "duration"),
                chaosSet(
                        getInt(chaosSetMap, "httpAbort"),
                        getInt(chaosSetMap, "httpDelay"),
                        getInt(chaosSetMap, "ioChaosTimeout"),
                        getInt(chaosSetMap, "podChaosTimeout")
                )
        );
    }

    public void runStep(OSDFPaths paths, OCExecutor ocExecutor) {
        announce("Step " + number + " started.");
        long start = nanoTime();
        ChaosRunnersLoader loader = ChaosRunnersLoader.init(paths, ocExecutor);
        chaosSet.faults().forEach(type -> loader.byType(type).run(components, chaosSet, severity, duration));

        //wait if finished earlier
        long current = nanoTime();
        sleepSec(duration - TimeUnit.NANOSECONDS.toSeconds(current - start));

        //If faults were injected at this step, remove them
        announce("Step " + number + " cleaning up.");
        chaosSet.faults().forEach(type -> loader.byType(type).stop(components));
        announce("Step " + number + " finished.");
    }

    public void checkValues() {
        if (number == null) {
            throw new OSDFException("Assign numbers for all steps in test plan!");
        }

        if (severity == null) {
            throw new OSDFException("Assign severity for step " + number);
        }

        if (severity < 0 || severity > 100) {
            throw new OSDFException("Incorrect severity " + severity + " in step " + number);
        }

        if (duration == null) {
            throw new OSDFException("Assign duration for step " + number);
        }

        if (duration < 0) {
            throw new OSDFException("Incorrect duration " + duration + " in step " + number);
        }
    }
}

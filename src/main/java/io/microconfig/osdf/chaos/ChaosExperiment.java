package io.microconfig.osdf.chaos;

import io.microconfig.osdf.chaos.components.ChaosComponent;
import io.microconfig.osdf.chaos.types.Chaos;
import io.microconfig.osdf.chaos.validators.BasicValidator;
import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.microconfig.osdf.chaos.DurationParams.fromYaml;
import static io.microconfig.osdf.chaos.types.Chaos.getAllChaosImpls;
import static io.microconfig.osdf.chaos.types.Chaos.parameterizedChaosList;
import static io.microconfig.osdf.chaos.validators.BasicValidator.basicValidator;
import static io.microconfig.osdf.chaos.validators.PodAndIOChaosIntersectionValidator.podAndIOChaosIntersectionValidator;
import static io.microconfig.osdf.utils.YamlUtils.getMap;
import static io.microconfig.osdf.utils.YamlUtils.loadFromPath;
import static io.microconfig.utils.Logger.announce;
import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Collectors.toUnmodifiableSet;

@RequiredArgsConstructor
public class ChaosExperiment {
    private final Set<List<Chaos>> chaosSet;
    private final DurationParams durationParams;

    public static ChaosExperiment chaosExperiment(OSDFPaths paths, ClusterCLI cli, ChaosComponent component) {
        DurationParams durationParams = fromYaml(loadFromPath(component.getPathToPlan()));
        Map<String, Object> rules = getMap(loadFromPath(component.getPathToPlan()), "rules");
        Set<List<Chaos>> chaosSet = rules.entrySet().stream().map(entry -> parameterizedChaosList(paths, cli, entry, durationParams)).collect(toSet());
        return new ChaosExperiment(chaosSet, durationParams);
    }

    public static void stop(OSDFPaths paths, ClusterCLI cli) {
        getAllChaosImpls(paths, cli).forEach(Chaos::forceStop);
    }

    public void check() {
        BasicValidator basicValidator = basicValidator(chaosSet);
        basicValidator.basicCheck();
        basicValidator.checkPodChaosIntersections();
        basicValidator.checkNetworkChaosIntersections();
        podAndIOChaosIntersectionValidator(chaosSet).podAndIOChaosIntersectionCheck();
    }

    public void run() {
        announce("Launch of chaos");
        for (int stage = 0; stage < durationParams.getStagesNum(); stage++) {
            announce("Launch of chaos stage " + (stage + 1));
            int finalStage = stage;
            Set<Chaos> currentStageChaosSet = chaosSet.stream().map(list -> list.get(finalStage)).collect(toUnmodifiableSet());
            currentStageChaosSet.forEach(Chaos::run);
            try {
                sleep(durationParams.getStageDurationInMillis());
            } catch (InterruptedException e) {
                currentStageChaosSet.forEach(Chaos::forceStop);
                currentThread().interrupt();
                throw new OSDFException("Chaos experiment was interrupted on stage " + stage, e);
            }
            currentStageChaosSet.forEach(Chaos::stop);
            announce("Chaos stage " + (stage + 1) + " stopped");
        }
        announce("Chaos stopped");
    }
}

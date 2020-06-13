package io.microconfig.osdf.chaos;

import io.microconfig.osdf.chaos.components.ChaosComponent;
import io.microconfig.osdf.chaos.metrics.Metric;
import io.microconfig.osdf.chaos.types.Chaos;
import io.microconfig.osdf.chaos.validators.BasicValidator;
import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.metrics.MetricsPuller;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static io.microconfig.osdf.chaos.ChaosListLoader.chaosListLoader;
import static io.microconfig.osdf.chaos.DurationParams.fromYaml;
import static io.microconfig.osdf.chaos.metrics.MetricsChecker.metricsChecker;
import static io.microconfig.osdf.chaos.metrics.MetricsConfigParser.metricsConfigParser;
import static io.microconfig.osdf.chaos.types.Chaos.getAllChaosImpls;
import static io.microconfig.osdf.chaos.validators.BasicValidator.basicValidator;
import static io.microconfig.osdf.chaos.validators.PodAndIOChaosIntersectionValidator.podAndIOChaosIntersectionValidator;
import static io.microconfig.osdf.utils.YamlUtils.getMap;
import static io.microconfig.osdf.utils.YamlUtils.loadFromPath;
import static io.microconfig.utils.Logger.announce;
import static io.microconfig.utils.Logger.error;
import static java.lang.Thread.currentThread;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Collectors.toUnmodifiableSet;

@RequiredArgsConstructor
public class ChaosExperiment {
    private final Integer timeout = 2;
    private final Set<List<Chaos>> chaosSet;
    private final DurationParams durationParams;
    private final Set<Metric> metricsSet;
    private final MetricsPuller puller;

    public static ChaosExperiment chaosExperiment(OSDFPaths paths, ClusterCLI cli, ChaosComponent component) {
        DurationParams durationParams = fromYaml(loadFromPath(component.getPathToPlan()));
        Map<String, Object> rules = getMap(loadFromPath(component.getPathToPlan()), "rules");

        MetricsPuller puller = metricsConfigParser().buildPuller(loadFromPath(component.getPathToPlan()));
        Set<Metric> metricsSet = metricsConfigParser().fromYaml(loadFromPath(component.getPathToPlan()));

        ChaosListLoader loader = chaosListLoader(paths, cli, durationParams);
        Set<List<Chaos>> chaosSet = rules.entrySet().stream().map(loader::loadChaosList).collect(toSet());
        return new ChaosExperiment(chaosSet, durationParams, metricsSet, puller);
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

        IntStream.range(0, durationParams.getStagesNum()).forEach(stage -> {
            announce("Launch of chaos stage " + (stage + 1));
            Set<Chaos> currentStageChaosSet = chaosSet.stream().map(list -> list.get(stage)).collect(toUnmodifiableSet());
            currentStageChaosSet.forEach(Chaos::run);
            try {
                metricsChecker(durationParams.getStageDurationInSec(), timeout, puller, metricsSet).run();
            } catch (Exception e) {
                error(e.getMessage());
                currentStageChaosSet.forEach(Chaos::forceStop);
                currentThread().interrupt();
                throw new OSDFException("Chaos experiment was interrupted on stage " + stage, e);
            }
            currentStageChaosSet.forEach(Chaos::stop);
            announce("Chaos stage " + (stage + 1) + " stopped");
        });
        announce("Chaos stopped");
    }
}
package io.microconfig.osdf.chaos;

import io.microconfig.osdf.chaos.components.ChaosComponent;
import io.microconfig.osdf.chaos.types.Chaos;
import io.microconfig.osdf.chaos.validators.BasicValidator;
import io.cluster.old.cluster.cli.ClusterCli;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.metrics.Metric;
import io.microconfig.osdf.metrics.MetricsPuller;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.microconfig.osdf.chaos.ChaosListLoader.chaosListLoader;
import static io.microconfig.osdf.chaos.DurationParams.fromYaml;
import static io.microconfig.osdf.chaos.types.Chaos.getAllChaosImpls;
import static io.microconfig.osdf.chaos.validators.BasicValidator.basicValidator;
import static io.microconfig.osdf.chaos.validators.PodAndIOChaosIntersectionValidator.podAndIOChaosIntersectionValidator;
import static io.microconfig.osdf.metrics.MetricsChecker.metricsChecker;
import static io.microconfig.osdf.metrics.MetricsConfigParser.metricsConfigParser;
import static io.microconfig.osdf.utils.YamlUtils.getMap;
import static io.microconfig.osdf.utils.YamlUtils.loadFromPath;
import static io.microconfig.utils.Logger.announce;
import static java.lang.Thread.currentThread;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static java.util.stream.IntStream.range;

@RequiredArgsConstructor
public class ChaosExperiment {
    private static final Integer TIMEOUT = 2;
    private final Set<List<Chaos>> chaosSet;
    private final DurationParams durationParams;
    private final Set<Metric> metricsSet;
    private final MetricsPuller puller;

    public static ChaosExperiment chaosExperiment(OsdfPaths paths, ClusterCli cli, ChaosComponent component) {
        Map<String, Object> componentMap = loadFromPath(component.getPathToPlan());

        DurationParams durationParams = fromYaml(componentMap);
        Map<String, Object> rules = getMap(componentMap, "rules");

        MetricsPuller puller = metricsConfigParser().buildPuller(getMap(componentMap, "monitoring"));
        Set<Metric> metricsSet = metricsConfigParser().fromYaml(getMap(componentMap, "monitoring"));

        ChaosListLoader loader = chaosListLoader(paths, cli, durationParams);
        Set<List<Chaos>> chaosSet = rules.entrySet().stream().map(loader::loadChaosList).collect(toSet());
        return new ChaosExperiment(chaosSet, durationParams, metricsSet, puller);
    }

    public static void stop(OsdfPaths paths, ClusterCli cli) {
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
        range(0, durationParams.getStagesNum()).forEach(this::runStage);
        announce("Chaos stopped");
    }

    private void runStage(int stage) {
        announce("Launch of chaos stage " + (stage + 1));
        Set<Chaos> currentStageChaosSet = chaosSet.stream().map(list -> list.get(stage)).collect(toUnmodifiableSet());
        currentStageChaosSet.forEach(Chaos::run);
        checkMetrics(stage, currentStageChaosSet);
        currentStageChaosSet.forEach(Chaos::stop);
        announce("Chaos stage " + (stage + 1) + " stopped");
    }

    private void checkMetrics(int stage, Set<Chaos> currentStageChaosSet) {
        try {
            if (!metricsChecker(durationParams.getStageDurationInSec(), TIMEOUT, puller, metricsSet).runCheck()) {
                throw new OSDFException("Metrics check failed");
            }
        } catch (Exception e) {
            currentStageChaosSet.forEach(Chaos::forceStop);
            currentThread().interrupt();
            throw new OSDFException("Chaos experiment was interrupted on stage " + (stage + 1), e);
        }
    }
}
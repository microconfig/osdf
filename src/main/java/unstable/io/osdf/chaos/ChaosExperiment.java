package unstable.io.osdf.chaos;

import unstable.io.osdf.chaos.components.ChaosComponent;
import unstable.io.osdf.chaos.types.Chaos;
import unstable.io.osdf.chaos.validators.BasicValidator;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.common.exceptions.OSDFException;
import unstable.io.osdf.metrics.Metric;
import unstable.io.osdf.metrics.MetricsPuller;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;
import unstable.io.osdf.chaos.validators.PodAndIOChaosIntersectionValidator;
import unstable.io.osdf.metrics.MetricsChecker;
import unstable.io.osdf.metrics.MetricsConfigParser;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static unstable.io.osdf.chaos.ChaosListLoader.chaosListLoader;
import static unstable.io.osdf.chaos.DurationParams.fromYaml;
import static io.osdf.common.utils.YamlUtils.getMap;
import static io.osdf.common.utils.YamlUtils.loadFromPath;
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

        MetricsPuller puller = MetricsConfigParser.metricsConfigParser().buildPuller(getMap(componentMap, "monitoring"));
        Set<Metric> metricsSet = MetricsConfigParser.metricsConfigParser().fromYaml(getMap(componentMap, "monitoring"));

        ChaosListLoader loader = chaosListLoader(paths, cli, durationParams);
        Set<List<Chaos>> chaosSet = rules.entrySet().stream().map(loader::loadChaosList).collect(toSet());
        return new ChaosExperiment(chaosSet, durationParams, metricsSet, puller);
    }

    public static void stop(OsdfPaths paths, ClusterCli cli) {
        Chaos.getAllChaosImpls(paths, cli).forEach(Chaos::forceStop);
    }

    public void check() {
        BasicValidator basicValidator = BasicValidator.basicValidator(chaosSet);
        basicValidator.basicCheck();
        basicValidator.checkPodChaosIntersections();
        basicValidator.checkNetworkChaosIntersections();
        PodAndIOChaosIntersectionValidator.podAndIOChaosIntersectionValidator(chaosSet).podAndIOChaosIntersectionCheck();
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
            if (!MetricsChecker.metricsChecker(durationParams.getStageDurationInSec(), TIMEOUT, puller, metricsSet).runCheck()) {
                throw new OSDFException("Metrics check failed");
            }
        } catch (Exception e) {
            currentStageChaosSet.forEach(Chaos::forceStop);
            currentThread().interrupt();
            throw new OSDFException("Chaos experiment was interrupted on stage " + (stage + 1), e);
        }
    }
}
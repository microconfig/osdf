package io.microconfig.osdf.chaos.types;

import io.microconfig.osdf.chaos.DurationParams;
import io.microconfig.osdf.chaos.ParamsExtractor;
import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.cluster.pod.Pod;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.service.deployment.pack.ServiceDeployPack;
import io.microconfig.osdf.utils.YamlUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.microconfig.osdf.chaos.ParamsExtractor.paramsExtractor;
import static io.microconfig.osdf.chaos.types.ChaosMode.*;
import static io.microconfig.osdf.chaos.types.ChaosType.POD;
import static io.microconfig.osdf.service.deployment.pack.loader.DefaultServiceDeployPacksLoader.defaultServiceDeployPacksLoader;
import static io.microconfig.osdf.utils.YamlUtils.getList;
import static io.microconfig.osdf.utils.YamlUtils.getObjectOrNull;
import static io.microconfig.utils.Logger.announce;
import static java.lang.Math.floorDiv;
import static java.util.Set.of;
import static java.util.stream.IntStream.range;

@EqualsAndHashCode
@RequiredArgsConstructor
public class PodChaos implements Chaos {
    private static final String PARAMS = "params";
    private static final Set<ChaosMode> SUPPORTED_MODES = of(PERCENT, FIXED);
    private final OSDFPaths paths;
    private final ClusterCLI cli;
    @Getter
    private final String name;
    @Getter
    private final List<String> components;
    private final Integer timeout;
    private final Integer severity;
    private final ChaosMode mode;

    @EqualsAndHashCode.Exclude
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    public static PodChaos emptyPodChaos(OSDFPaths paths, ClusterCLI cli) {
        return new PodChaos(paths, cli, "emptyPod", null, null, null, null);
    }

    @SuppressWarnings("unchecked")
    public static List<Chaos> parameterizedPodChaos(OSDFPaths paths, ClusterCLI cli, Map.Entry<String, Object> entry, DurationParams durationParams) {
        String name = entry.getKey();
        Map<String, Object> yaml = (Map<String, Object>) entry.getValue();
        List<String> components = (List<String>) (Object) getList(yaml, "components");
        ParamsExtractor extractor = paramsExtractor();
        List<Integer> severities = extractor.intParamToList(getObjectOrNull(yaml, PARAMS, "severity"), durationParams.getStagesNum());
        List<Integer> timeouts = extractor.intParamToList(getObjectOrNull(yaml, PARAMS, "timeout"), durationParams.getStagesNum());
        ChaosMode mode = valueOf(YamlUtils.getString(yaml, "mode").toUpperCase());
        List<Chaos> chaosList = new ArrayList<>();
        return range(0, durationParams.getStagesNum())
                .mapToObj(i -> {
                    String chaosName = name + "-" + (i + 1);
                    return new PodChaos(paths, cli, chaosName, components, timeouts.get(i), severities.get(i), mode);
                })
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void run() {
        service.scheduleAtFixedRate(this::kill, 0, timeout, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        service.shutdownNow();
    }

    @Override
    public void forceStop() {
        stop();
    }

    private void kill() {
        List<ServiceDeployPack> deployPacks = defaultServiceDeployPacksLoader(paths, components, cli).loadPacks();
        deployPacks.forEach(
                pack -> pack.deployment().pods()
                        .stream()
                        .limit(calcLimit(pack.deployment().pods().size(), severity, mode))
                        .forEach(this::killPodAndAnnounce)
        );
    }

    private int calcLimit(int size, int severity, ChaosMode mode) {
        return mode == PERCENT ? floorDiv(size * severity, 100) : severity;
    }

    private void killPodAndAnnounce(Pod pod) {
        pod.forceDelete();
        announce(name + ":\t " + pod.getName() + " killed.");
    }

    @Override
    public void check() {
        if (!SUPPORTED_MODES.contains(mode)) {
            throw new OSDFException("Unsupported mode " + mode + " for " + name);
        }
        if (timeout == null || timeout < 1) {
            throw new OSDFException("Timeout is incorrect or missing in " + name);
        }
        if (severity == null || severity < 1 || (severity > 100 && mode != FIXED)) {
            throw new OSDFException("Severity is incorrect or missing in " + name);
        }
    }

    @Override
    public ChaosType type() {
        return POD;
    }
}

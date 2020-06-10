package io.microconfig.osdf.chaos.types;

import io.microconfig.osdf.chaos.DurationParams;
import io.microconfig.osdf.chaos.ParamsExtractor;
import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.cluster.pod.Pod;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.service.deployment.pack.ServiceDeployPack;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static io.microconfig.osdf.chaos.ParamsExtractor.paramsExtractor;
import static io.microconfig.osdf.chaos.types.ChaosMode.*;
import static io.microconfig.osdf.chaos.types.ChaosType.IO;
import static io.microconfig.osdf.service.deployment.pack.loader.DefaultServiceDeployPacksLoader.serviceLoader;
import static io.microconfig.osdf.service.loaders.filters.RequiredComponentsFilter.requiredComponentsFilter;
import static io.microconfig.osdf.utils.YamlUtils.*;
import static io.microconfig.utils.Logger.announce;
import static java.lang.Math.floorDiv;
import static java.util.Set.of;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.IntStream.range;

@EqualsAndHashCode
@RequiredArgsConstructor
public class IOChaos implements Chaos {
    private static final Set<ChaosMode> SUPPORTED_MODES = of(PERCENT, FIXED);
    private final OSDFPaths paths;
    private final ClusterCLI cli;
    @Getter
    private final String name;
    @Getter
    private final List<String> components;
    private final Integer severity;
    private final Long timeout;
    private final String volumePath;
    private final ChaosMode mode;

    public static IOChaos emptyIoChaos(OSDFPaths paths, ClusterCLI cli) {
        return new IOChaos(paths, cli, "EmptyIO", null, null, null, null, null);
    }

    @SuppressWarnings("unchecked")
    public static List<Chaos> parameterizedIOChaos(OSDFPaths paths, ClusterCLI cli, Entry<String, Object> entry, DurationParams durationParams) {
        String name = entry.getKey();
        Map<String, Object> yaml = (Map<String, Object>) entry.getValue();
        List<String> components = (List<String>) (Object) getList(yaml, "components");

        ParamsExtractor extractor = paramsExtractor();
        List<Integer> severities = extractor.intParamToList(getObjectOrNull(yaml, "params", "severity"), durationParams.getStagesNum());
        List<String> volumePaths = extractor.strParamToList(getObjectOrNull(yaml, "params", "path"), durationParams.getStagesNum());

        ChaosMode mode = valueOf(getString(yaml, "mode").toUpperCase());
        return range(0, durationParams.getStagesNum())
                .mapToObj(i -> {
                    String chaosName = name + "-" + (i + 1);
                    return new IOChaos(paths, cli, chaosName, components, severities.get(i), durationParams.getStageDurationInSec(), volumePaths.get(i), mode);
                })
                .collect(toUnmodifiableList());
    }

    private int calcLimit(int size, int severity, ChaosMode mode) {
        return mode == PERCENT ? floorDiv(size * severity, 100) : severity;
    }

    @Override
    public void run() {
        List<ServiceDeployPack> deployPacks = serviceLoader(paths, requiredComponentsFilter(components), cli).loadPacks();
        deployPacks.forEach(pack -> pack.deployment().pods()
                .parallelStream()
                .filter(Pod::checkStressContainer)
                .limit(calcLimit(pack.deployment().pods().size(), severity, mode))
                .forEach(this::runAndAnnounce));
    }

    private void runAndAnnounce(Pod pod) {
        cli.execute(getRunCommand(pod));
        announce(name + ":\t IO chaos launched in " + pod.getName() + " for " + timeout + "s");
    }

    private String getRunCommand(Pod pod) {
        return "exec " + pod.getName() + " -c stress-sidecar -- /bin/sh -c \"cd "+ volumePath +"; stress-ng --all 0 -t "
                + timeout + "s --class io\" &";
    }

    @Override
    public void stop() {
        //no implementation needed
    }

    @Override
    public void forceStop() {
        List<ServiceDeployPack> deployPacks = serviceLoader(paths, requiredComponentsFilter(components), cli).loadPacks();
        deployPacks.forEach(pack -> pack.deployment().pods()
                .stream()
                .filter(Pod::checkStressContainer)
                .forEach(pod -> {
                    cli.execute("exec " + pod.getName() + " -c stress-sidecar -- /bin/sh -c \"kill 1\"");
                    announce(name + ":\t IO chaos stopped in " + pod.getName());
                }));
    }

    @Override
    public void check() {
        if (!SUPPORTED_MODES.contains(mode)) {
            throw new OSDFException("Unsupported mode " + mode + " for " + name);
        }
        if (severity == null || severity < 1 || (severity > 100 && mode != FIXED)) {
            throw new OSDFException("Severity is incorrect or missing in " + name);
        }
        if (timeout == null || timeout < 1) {
            throw new OSDFException("Timeout is incorrect or missing in " + name);
        }
        if (components == null || components.isEmpty()) {
            throw new OSDFException("List of components is empty or missing in : " + name);
        }
    }

    @Override
    public ChaosType type() {
        return IO;
    }
}

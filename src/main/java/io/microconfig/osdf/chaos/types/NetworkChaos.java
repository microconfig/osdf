package io.microconfig.osdf.chaos.types;

import io.microconfig.osdf.chaos.DurationParams;
import io.microconfig.osdf.chaos.NetworkChaosInjector;
import io.microconfig.osdf.chaos.ParamsExtractor;
import io.cluster.old.cluster.cli.ClusterCLI;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.istio.Fault;
import io.osdf.settings.paths.OSDFPaths;
import io.microconfig.osdf.service.deployment.pack.ServiceDeployPack;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static io.microconfig.osdf.chaos.NetworkChaosInjector.chaosInjector;
import static io.microconfig.osdf.chaos.ParamsExtractor.paramsExtractor;
import static io.microconfig.osdf.chaos.types.ChaosType.NETWORK;
import static io.microconfig.osdf.istio.Fault.fault;
import static io.microconfig.osdf.service.deployment.pack.loader.DefaultServiceDeployPacksLoader.serviceLoader;
import static io.microconfig.osdf.service.istio.IstioService.isIstioService;
import static io.microconfig.osdf.service.loaders.filters.RequiredComponentsFilter.requiredComponentsFilter;
import static io.microconfig.osdf.utils.YamlUtils.getList;
import static io.microconfig.osdf.utils.YamlUtils.getObjectOrNull;
import static io.microconfig.utils.Logger.announce;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.IntStream.range;

@EqualsAndHashCode
@RequiredArgsConstructor
public class NetworkChaos implements Chaos {
    private static final String PARAMS = "params";
    private final OSDFPaths paths;
    private final ClusterCLI cli;
    @Getter
    private final String name;
    @Getter
    private final List<String> components;
    private final Fault fault;

    @SuppressWarnings("unchecked")
    public static List<Chaos> parameterizedNetworkChaos(OSDFPaths paths, ClusterCLI cli, Map.Entry<String, Object> entry, DurationParams durationParams) {
        String name = entry.getKey();
        Map<String, Object> yaml = (Map<String, Object>) entry.getValue();
        List<String> components = (List<String>) (Object) getList(yaml, "components");

        ParamsExtractor extractor = paramsExtractor();

        List<Integer> abortCodes = extractor.intParamToListOrEmpty(getObjectOrNull(yaml, PARAMS, "http-error", "code"), durationParams.getStagesNum());
        List<Integer> abortPercentages = extractor.intParamToListOrEmpty(getObjectOrNull(yaml, PARAMS, "http-error", "percentage"), durationParams.getStagesNum());
        List<Integer> delays = extractor.intParamToListOrEmpty(getObjectOrNull(yaml, PARAMS, "http-delay", "delay"), durationParams.getStagesNum());
        List<Integer> delayPercentages = extractor.intParamToListOrEmpty(getObjectOrNull(yaml, PARAMS, "http-delay", "percentage"), durationParams.getStagesNum());

        return range(0, durationParams.getStagesNum())
                .mapToObj(i -> {
                    String chaosName = name + "-" + (i + 1);
                    Fault fault = fault(
                            abortCodes.isEmpty() ? null : abortCodes.get(i),
                            abortPercentages.isEmpty() ? null : abortPercentages.get(i),
                            delays.isEmpty() ? null : delays.get(i),
                            delayPercentages.isEmpty() ? null : delayPercentages.get(i)
                    );
                    return new NetworkChaos(paths, cli, chaosName, components, fault);
                }).collect(toUnmodifiableList());
    }

    public static NetworkChaos emptyNetworkChaos(OSDFPaths paths, ClusterCLI cli) {
        return new NetworkChaos(paths, cli, "emptyNetwork", null, null);
    }

    @Override
    public void run() {
        deploy(components, fault);
    }

    @Override
    public void stop() {
        deploy(components, null);
    }

    @Override
    public void forceStop() {
        stop();
    }

    private void deploy(List<String> components, Fault fault) {
        NetworkChaosInjector deployer = chaosInjector(fault);
        List<ServiceDeployPack> deployPacks = serviceLoader(paths, requiredComponentsFilter(components), cli).loadPacks();
        deployPacks.parallelStream()
                .filter(pack -> isIstioService(pack.service()))
                .forEach(pack -> announce(name + ":\t" + deployer.inject(pack.service())));
    }

    @Override
    public void check() {
        try {
            fault.checkCorrectness();
        } catch (OSDFException e) {
            throw new OSDFException("Incorrect fault in " + name, e);
        }
        if (components == null || components.isEmpty()) {
            throw new OSDFException("List of components is empty or missing in: " + name);
        }
    }

    @Override
    public ChaosType type() {
        return NETWORK;
    }
}

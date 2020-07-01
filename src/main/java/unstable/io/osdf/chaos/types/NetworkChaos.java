package unstable.io.osdf.chaos.types;

import unstable.io.osdf.chaos.DurationParams;
import unstable.io.osdf.chaos.NetworkChaosInjector;
import unstable.io.osdf.chaos.ParamsExtractor;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.common.exceptions.OSDFException;
import unstable.io.osdf.istio.Fault;
import io.osdf.settings.paths.OsdfPaths;
import io.osdf.core.service.core.deployment.pack.ServiceDeployPack;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static unstable.io.osdf.chaos.NetworkChaosInjector.chaosInjector;
import static unstable.io.osdf.chaos.ParamsExtractor.paramsExtractor;
import static unstable.io.osdf.chaos.types.ChaosType.NETWORK;
import static io.osdf.core.service.core.deployment.pack.loader.DefaultServiceDeployPacksLoader.serviceLoader;
import static io.osdf.core.service.cluster.types.istio.IstioService.isIstioService;
import static io.osdf.core.service.local.loaders.filters.RequiredComponentsFilter.requiredComponentsFilter;
import static io.osdf.common.utils.YamlUtils.getList;
import static io.osdf.common.utils.YamlUtils.getObjectOrNull;
import static io.microconfig.utils.Logger.announce;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.IntStream.range;

@EqualsAndHashCode
@RequiredArgsConstructor
public class NetworkChaos implements Chaos {
    private static final String PARAMS = "params";
    private final OsdfPaths paths;
    private final ClusterCli cli;
    @Getter
    private final String name;
    @Getter
    private final List<String> components;
    private final Fault fault;

    @SuppressWarnings("unchecked")
    public static List<Chaos> parameterizedNetworkChaos(OsdfPaths paths, ClusterCli cli, Map.Entry<String, Object> entry, DurationParams durationParams) {
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
                    Fault fault = Fault.fault(
                            abortCodes.isEmpty() ? null : abortCodes.get(i),
                            abortPercentages.isEmpty() ? null : abortPercentages.get(i),
                            delays.isEmpty() ? null : delays.get(i),
                            delayPercentages.isEmpty() ? null : delayPercentages.get(i)
                    );
                    return new NetworkChaos(paths, cli, chaosName, components, fault);
                }).collect(toUnmodifiableList());
    }

    public static NetworkChaos emptyNetworkChaos(OsdfPaths paths, ClusterCli cli) {
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

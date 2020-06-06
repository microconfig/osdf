package io.microconfig.osdf.chaos.types;

import io.microconfig.osdf.chaos.DurationParams;
import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.utils.YamlUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static io.microconfig.osdf.chaos.types.ChaosType.valueOf;
import static io.microconfig.osdf.chaos.types.IOChaos.emptyIoChaos;
import static io.microconfig.osdf.chaos.types.IOChaos.parameterizedIOChaos;
import static io.microconfig.osdf.chaos.types.NetworkChaos.emptyNetworkChaos;
import static io.microconfig.osdf.chaos.types.NetworkChaos.parameterizedNetworkChaos;
import static io.microconfig.osdf.chaos.types.PodChaos.emptyPodChaos;
import static io.microconfig.osdf.chaos.types.PodChaos.parameterizedPodChaos;
import static io.microconfig.utils.Logger.announce;
import static java.util.Collections.emptyList;

public interface Chaos {
    static List<Chaos> getAllChaosImpls(OSDFPaths paths, ClusterCLI cli) {
        return List.of(
                emptyIoChaos(paths, cli),
                emptyNetworkChaos(paths, cli),
                emptyPodChaos(paths, cli)
        );
    }

    @SuppressWarnings("unchecked")
    static List<Chaos> parameterizedChaosList(OSDFPaths paths, ClusterCLI cli, Entry<String, Object> entry, DurationParams durationParams) {
        Map<String, Object> yaml = (Map<String, Object>) entry.getValue();
        ChaosType type = valueOf(YamlUtils.getString(yaml, "type").toUpperCase());
        String name = entry.getKey();
        switch (type) {
            case IO:
                return parameterizedIOChaos(paths, cli, entry, durationParams);
            case NETWORK:
                return parameterizedNetworkChaos(paths, cli, entry, durationParams);
            case POD:
                return parameterizedPodChaos(paths, cli, entry, durationParams);
            default:
                throw new OSDFException("Unknown type of chaos: " + name);
        }
    }

    @SuppressWarnings("unchecked")
    static List<Integer> intParamToList(Object o, Integer stagesNum) throws OSDFException { // is trows needed?
        if (o instanceof List) {
            List<Integer> paramList = (List<Integer>) o;
            if (paramList.size() == stagesNum) {
                return paramList;
            }
            throw new OSDFException("Wrong num of params. Expected: " + stagesNum + ", actual: " + paramList.size());
        }
        if (o instanceof Integer) {
            Integer value = (Integer) o;
            List<Integer> paramList = new ArrayList<>();
            for (int i = 0; i < stagesNum; i++) {
                paramList.add(value);
            }
            return paramList;
        }
        throw new OSDFException("Can't parse param");
    }

    static List<Integer> intParamToListOrEmpty(Object o, Integer stagesNum) { // is trows needed?
        if (o == null) return emptyList();
        return intParamToList(o, stagesNum);
    }

    static void announceStopped(String name) {
        announce(name + ":\t stopped.");
    }

    static void announceLaunching(String name) {
        announce(name + ":\t launching.");
    }

    void run();

    void stop();

    void forceStop();

    void check();

    List<String> getComponents();

    String getName();

    ChaosType type();
}
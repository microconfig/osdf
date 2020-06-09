package io.microconfig.osdf.chaos;

import io.microconfig.osdf.chaos.types.Chaos;
import io.microconfig.osdf.chaos.types.ChaosType;
import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static io.microconfig.osdf.chaos.types.ChaosType.valueOf;
import static io.microconfig.osdf.chaos.types.IOChaos.parameterizedIOChaos;
import static io.microconfig.osdf.chaos.types.NetworkChaos.parameterizedNetworkChaos;
import static io.microconfig.osdf.chaos.types.PodChaos.parameterizedPodChaos;
import static io.microconfig.osdf.utils.YamlUtils.getString;

@RequiredArgsConstructor
public class ChaosListLoader {
    private final OSDFPaths paths;
    private final ClusterCLI cli;
    private final DurationParams durationParams;

    public static ChaosListLoader chaosListLoader(OSDFPaths paths, ClusterCLI cli, DurationParams durationParams) {
        return new ChaosListLoader(paths, cli, durationParams);
    }

    @SuppressWarnings("unchecked")
    public List<Chaos> loadChaosList(Map.Entry<String, Object> entry) {
        Map<String, Object> yaml = (Map<String, Object>) entry.getValue();
        ChaosType type = valueOf(getString(yaml, "type").toUpperCase());
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
}

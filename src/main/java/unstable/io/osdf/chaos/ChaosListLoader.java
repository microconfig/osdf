package unstable.io.osdf.chaos;

import unstable.io.osdf.chaos.types.Chaos;
import unstable.io.osdf.chaos.types.ChaosType;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;
import unstable.io.osdf.chaos.types.*;

import java.util.List;
import java.util.Map;

import static unstable.io.osdf.chaos.types.ChaosType.valueOf;
import static io.osdf.common.utils.YamlUtils.getString;

@RequiredArgsConstructor
public class ChaosListLoader {
    private final OsdfPaths paths;
    private final ClusterCli cli;
    private final DurationParams durationParams;

    public static ChaosListLoader chaosListLoader(OsdfPaths paths, ClusterCli cli, DurationParams durationParams) {
        return new ChaosListLoader(paths, cli, durationParams);
    }

    @SuppressWarnings("unchecked")
    public List<Chaos> loadChaosList(Map.Entry<String, Object> entry) {
        Map<String, Object> yaml = (Map<String, Object>) entry.getValue();
        ChaosType type = ChaosType.valueOf(getString(yaml, "type").toUpperCase());
        String name = entry.getKey();
        switch (type) {
            case IO:
                return IOChaos.parameterizedIOChaos(paths, cli, entry, durationParams);
            case NETWORK:
                return NetworkChaos.parameterizedNetworkChaos(paths, cli, entry, durationParams);
            case POD:
                return PodChaos.parameterizedPodChaos(paths, cli, entry, durationParams);
            default:
                throw new OSDFException("Unknown type of chaos: " + name);
        }
    }
}

package io.microconfig.osdf.chaos.types;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.utils.YamlUtils;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static io.microconfig.osdf.chaos.types.IOChaos.emptyIoChaos;
import static io.microconfig.osdf.chaos.types.IOChaos.ioChaos;
import static io.microconfig.osdf.chaos.types.NetworkChaos.emptyNetworkChaos;
import static io.microconfig.osdf.chaos.types.NetworkChaos.networkChaos;
import static io.microconfig.osdf.chaos.types.PodChaos.emptyPodChaos;
import static io.microconfig.osdf.chaos.types.PodChaos.podChaos;

public interface Chaos {
    static List<Chaos> getAllChaosImpls() {
        return List.of(
                emptyIoChaos(),
                emptyNetworkChaos(),
                emptyPodChaos()
        );
    }

    @SuppressWarnings("unchecked")
    static Chaos chaos(Entry<String, Object> entry) {
        Map<String, Object> yaml = (Map<String, Object>) entry.getValue();
        String type = YamlUtils.getString(yaml, "type");
        String name = entry.getKey();
        switch (type) {
            case "io":
                return ioChaos(name, yaml);
            case "network":
                return networkChaos(name, yaml);
            case "pod":
                return podChaos(name, yaml);
            default:
                throw new OSDFException("Unknown type of chaos: " + name);
        }
    }

    void run(OSDFPaths paths, ClusterCLI cli);

    void stop(OSDFPaths paths, ClusterCLI cli);

    void check();

    List<String> getComponents();

    String getName();

    String type();
}
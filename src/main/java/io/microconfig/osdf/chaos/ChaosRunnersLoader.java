package io.microconfig.osdf.chaos;

import io.microconfig.osdf.chaos.chaosRunners.ChaosRunner;
import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static io.microconfig.osdf.chaos.chaosRunners.IOChaosRunner.ioChaosRunner;
import static io.microconfig.osdf.chaos.chaosRunners.NetworkChaosRunner.networkChaosRunner;
import static io.microconfig.osdf.chaos.chaosRunners.PodChaosRunner.podChaosRunner;


@AllArgsConstructor
public class ChaosRunnersLoader {

    final private Map<String, ChaosRunner> implementations;

    static public ChaosRunnersLoader init(OSDFPaths paths, ClusterCLI cli) {
        Map<String, ChaosRunner> implementations = new HashMap<>();
        implementations.put("io", ioChaosRunner(paths, cli));
        implementations.put("network", networkChaosRunner(paths, cli));
        implementations.put("pods", podChaosRunner(paths, cli));
        return new ChaosRunnersLoader(implementations);
    }

    public ChaosRunner byType(String type) {
        if (implementations.containsKey(type)) {
            return implementations.get(type);
        }
        throw new OSDFException("implementation not found");
    }

    public Collection<ChaosRunner> getAllImplementations() {
        return implementations.values();
    }
}
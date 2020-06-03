package io.microconfig.osdf.api.implementations;

import io.microconfig.osdf.api.declarations.ChaosApi;
import io.microconfig.osdf.chaos.ChaosExperiment;
import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.AllArgsConstructor;

import static io.microconfig.osdf.chaos.ChaosExperiment.chaosExperiment;

@AllArgsConstructor
public class ChaosApiImpl implements ChaosApi {
    private final OSDFPaths paths;
    private final ClusterCLI cli;

    public static ChaosApi chaosApi(OSDFPaths paths, ClusterCLI cli) {
        return new ChaosApiImpl(paths, cli);
    }

    @Override
    public void runChaosExperiment() {
        chaosExperiment(paths, cli).run();
    }

    @Override
    public void stopChaos() {
        ChaosExperiment.stop(paths, cli);
    }
}

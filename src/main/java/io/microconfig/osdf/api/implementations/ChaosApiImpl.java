package io.microconfig.osdf.api.implementations;

import io.microconfig.osdf.api.declarations.ChaosApi;
import io.microconfig.osdf.chaos.ChaosRunnersLoader;
import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.commands.ChaosExperimentCommand;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChaosApiImpl implements ChaosApi {
    private final OSDFPaths paths;
    private final ClusterCLI cli;

    public static ChaosApi chaosApi(OSDFPaths paths, ClusterCLI cli) {
        return new ChaosApiImpl(paths, cli);
    }

    @Override
    public void runChaosExperiment() {
        new ChaosExperimentCommand(paths, cli).run();
    }

    @Override
    public void stopChaos() {
        ChaosRunnersLoader.init(paths, cli).getAllImplementations().forEach(impl -> impl.stop(null));
    }
}

package io.microconfig.osdf.api.implementations;

import io.microconfig.osdf.api.declarations.ChaosApi;
import io.microconfig.osdf.chaos.ChaosRunnersLoader;
import io.microconfig.osdf.commands.ChaosExperimentCommand;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChaosApiImpl implements ChaosApi {
    private final OSDFPaths paths;
    private final OCExecutor oc;

    public static ChaosApi chaosApi(OSDFPaths paths, OCExecutor oc) {
        return new ChaosApiImpl(paths, oc);
    }

    @Override
    public void runChaosExperiment() {
        new ChaosExperimentCommand(paths, oc).run();
    }

    @Override
    public void stopChaos() {
        ChaosRunnersLoader.init(paths, oc).getAllImplementations().forEach(impl -> impl.stop(null));
    }
}
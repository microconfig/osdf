package unstable.io.osdf;

import unstable.io.osdf.chaos.ChaosExperiment;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.AllArgsConstructor;

import static unstable.io.osdf.chaos.ChaosExperiment.chaosExperiment;
import static unstable.io.osdf.chaos.ChaosExperiment.stop;
import static unstable.io.osdf.chaos.loader.ChaosComponentLoader.chaosComponentLoader;

@AllArgsConstructor
public class ChaosApiImpl implements ChaosApi {
    private final OsdfPaths paths;
    private final ClusterCli cli;

    public static ChaosApi chaosApi(OsdfPaths paths, ClusterCli cli) {
        return new ChaosApiImpl(paths, cli);
    }

    @Override
    public void runChaosExperiment(String componentName) {
        ChaosExperiment experiment = chaosExperiment(paths, cli, chaosComponentLoader(paths).loadByName(componentName));
        experiment.check();
        experiment.run();
    }

    @Override
    public void stopChaos() {
        stop(paths, cli);
    }
}

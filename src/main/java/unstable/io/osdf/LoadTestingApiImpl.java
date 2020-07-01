package unstable.io.osdf;

import io.osdf.core.connection.cli.ClusterCli;
import unstable.io.osdf.loadtesting.configs.JmeterConfigProcessor;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;
import unstable.io.osdf.loadtesting.JmeterPlanPathGenerator;
import unstable.io.osdf.loadtesting.loader.JmeterPathLoader;

import java.nio.file.Path;

import static unstable.io.osdf.LoadTestCommand.loadTestCommand;
import static unstable.io.osdf.FindPeakLoadCommand.findPeakLoadCommand;

@RequiredArgsConstructor
public class LoadTestingApiImpl implements LoadTestingApi {
    private final OsdfPaths paths;
    private final ClusterCli cli;

    public static LoadTestingApi loadTestingApi(OsdfPaths paths, ClusterCli cli) {
        return new LoadTestingApiImpl(paths, cli);
    }

    @Override
    public void loadTest(Path jmeterPlanPath, String componentName, Integer numberOfSlaves) {
        int number = numberOfSlaves != null ? numberOfSlaves : 3;
        Path path = JmeterPathLoader.pathLoader(paths, componentName).jmeterComponentsPathLoad();
        JmeterConfigProcessor configProcessor = jmeterPlanPath != null ?
                JmeterConfigProcessor.configProcessor(path, number, jmeterPlanPath) :
                JmeterConfigProcessor.configProcessor(path, number, JmeterPlanPathGenerator.jmeterPlanPathGenerator(paths, cli, componentName).generate());
        configProcessor.init();
        loadTestCommand(cli, configProcessor).run();
    }

    @Override
    public void findPeakLoad(String componentName, Integer numberOfSlaves) {
        int number = numberOfSlaves != null ? numberOfSlaves : 3;
        Path path = JmeterPathLoader.pathLoader(paths, componentName).jmeterComponentsPathLoad();
        JmeterConfigProcessor configProcessor =
                JmeterConfigProcessor.configProcessor(path, number, JmeterPlanPathGenerator.jmeterPlanPathGenerator(paths, cli, componentName).generate());
        findPeakLoadCommand(cli, configProcessor).run();
    }
}

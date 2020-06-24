package io.microconfig.osdf.api.implementations;

import io.microconfig.osdf.api.declarations.LoadTestingApi;
import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.loadtesting.jmeter.configs.JmeterConfigProcessor;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.microconfig.osdf.commands.LoadTestCommand.loadTestCommand;
import static io.microconfig.osdf.commands.FindPeakLoadCommand.findPeakLoadCommand;
import static io.microconfig.osdf.loadtesting.jmeter.JmeterPlanPathGenerator.jmeterPlanPathGenerator;
import static io.microconfig.osdf.loadtesting.jmeter.configs.JmeterConfigProcessor.configProcessor;
import static io.microconfig.osdf.loadtesting.jmeter.loader.JmeterPathLoader.pathLoader;

@RequiredArgsConstructor
public class LoadTestingApiImpl implements LoadTestingApi {
    private final OSDFPaths paths;
    private final ClusterCLI cli;

    public static LoadTestingApi loadTestingApi(OSDFPaths paths, ClusterCLI cli) {
        return new LoadTestingApiImpl(paths, cli);
    }

    @Override
    public void loadTest(Path jmeterPlanPath, String componentName, Integer numberOfSlaves) {
        int number = numberOfSlaves != null ? numberOfSlaves : 3;
        Path path = pathLoader(paths, componentName).jmeterComponentsPathLoad();
        JmeterConfigProcessor configProcessor = jmeterPlanPath != null ?
                configProcessor(path, number, jmeterPlanPath) :
                configProcessor(path, number, jmeterPlanPathGenerator(paths, cli, componentName).generate());
        configProcessor.init();
        loadTestCommand(cli, configProcessor).run();
    }

    @Override
    public void findPeakLoad(String componentName, Integer numberOfSlaves) {
        int number = numberOfSlaves != null ? numberOfSlaves : 3;
        Path path = pathLoader(paths, componentName).jmeterComponentsPathLoad();
        JmeterConfigProcessor configProcessor =
                configProcessor(path, number, jmeterPlanPathGenerator(paths, cli, componentName).generate());
        findPeakLoadCommand(cli, configProcessor).run();
    }
}

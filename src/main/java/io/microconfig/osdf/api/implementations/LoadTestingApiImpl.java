package io.microconfig.osdf.api.implementations;

import io.microconfig.osdf.api.declarations.LoadTestingApi;
import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.microconfig.osdf.commands.LoadTestCommand.loadTestCommand;

@RequiredArgsConstructor
public class LoadTestingApiImpl implements LoadTestingApi {
    private final OSDFPaths paths;
    private final ClusterCLI cli;

    public static LoadTestingApi loadTestingApi(OSDFPaths paths, ClusterCLI cli) {
        return new LoadTestingApiImpl(paths, cli);
    }

    @Override
    public void loadTest(Path jmeterPlanPath, String configName, Integer numberOfSlaves) {
        if(configName == null && jmeterPlanPath == null)
            throw new RuntimeException("Please use --config to choose name of test config or" +
                    " --file parameter to mention path of jmeter jmx testplan");
        int number = numberOfSlaves != null ? numberOfSlaves : 3;
        if (configName != null) loadTestCommand(paths, cli, configName, number).run();
        if (jmeterPlanPath != null) loadTestCommand(paths, cli, jmeterPlanPath, number).run();
    }
}

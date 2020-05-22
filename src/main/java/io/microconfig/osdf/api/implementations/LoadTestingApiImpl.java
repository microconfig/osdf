package io.microconfig.osdf.api.implementations;

import io.microconfig.osdf.api.declarations.LoadTestingApi;
import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.commands.LoadTestCommand;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.microconfig.osdf.openshift.OpenShiftCLI.oc;

@RequiredArgsConstructor
public class LoadTestingApiImpl implements LoadTestingApi {
    private final OSDFPaths paths;
    private final ClusterCLI cli;

    public static LoadTestingApi loadTestingApi(OSDFPaths paths, ClusterCLI cli) {
        return new LoadTestingApiImpl(paths, cli);
    }

    @Override
    public void loadTest(Path jmeterPlanPath, Integer numberOfSlaves) {
        int number = numberOfSlaves != null ? numberOfSlaves : 3;
        new LoadTestCommand(paths, jmeterPlanPath, number, oc(cli)).run();
    }
}

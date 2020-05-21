package io.microconfig.osdf.api.implementations;

import io.microconfig.osdf.api.declarations.NewManagementApi;
import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.develop.commands.NewDeployCommand.deployCommand;

@RequiredArgsConstructor
public class NewManagementApiImpl implements NewManagementApi {
    private final OSDFPaths paths;
    private final ClusterCLI cli;

    public static NewManagementApi newManagementApi(OSDFPaths paths, ClusterCLI cli) {
        return new NewManagementApiImpl(paths, cli);
    }

    @Override
    public void deploy(List<String> components, String mode) {
        cli.login();
        deployCommand(paths, cli).deploy(components, mode);
    }
}

package io.microconfig.osdf.develop.commands;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.develop.deployers.DefaultClusterDeployer;
import io.microconfig.osdf.develop.service.deployment.pack.ServiceDeployPack;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.develop.deployers.DefaultClusterDeployer.defaultClusterDeployer;
import static io.microconfig.osdf.develop.service.deployment.pack.loader.DefaultServiceDeployPacksLoader.defaultServiceDeployPacksLoader;
import static io.microconfig.utils.Logger.announce;
import static java.util.stream.IntStream.range;

@RequiredArgsConstructor
public class NewDeployCommand {
    private final OSDFPaths paths;
    private final ClusterCLI cli;

    public static NewDeployCommand deployCommand(OSDFPaths paths, ClusterCLI cli) {
        return new NewDeployCommand(paths, cli);
    }

    public void deploy(List<String> serviceNames, String mode) {
        announce("Starting deployment. Mode is ignored for now");

        List<ServiceDeployPack> deployPacks = defaultServiceDeployPacksLoader(paths, serviceNames, cli).loadPacks();
        callDeployer(deployPacks);
    }

    private void callDeployer(List<ServiceDeployPack> deployPacks) {
        DefaultClusterDeployer deployer = defaultClusterDeployer(cli, paths);
        range(0, deployPacks.size()).forEach(i -> deployer.deploy(
                        deployPacks.get(i).service(),
                        deployPacks.get(i).deployment(),
                        deployPacks.get(i).files())
        );
    }
}

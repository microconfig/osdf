package io.microconfig.osdf.develop.commands;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.develop.deployers.DefaultServiceDeployer;
import io.microconfig.osdf.develop.jobrunner.DefaultJobRunner;
import io.microconfig.osdf.develop.service.deployment.pack.ServiceDeployPack;
import io.microconfig.osdf.develop.service.job.pack.ServiceJobPack;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.develop.deployers.DefaultServiceDeployer.defaultClusterDeployer;
import static io.microconfig.osdf.develop.jobrunner.DefaultJobRunner.defaultJobRunner;
import static io.microconfig.osdf.develop.service.deployment.pack.loader.DefaultServiceDeployPacksLoader.defaultServiceDeployPacksLoader;
import static io.microconfig.osdf.develop.service.job.pack.loader.DefaultServiceJobPackLoader.defaultServiceJobPackLoader;
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

        List<ServiceJobPack> jobPacks = defaultServiceJobPackLoader(paths, serviceNames, cli).loadPacks();
        callRunner(jobPacks);

        List<ServiceDeployPack> deployPacks = defaultServiceDeployPacksLoader(paths, serviceNames, cli).loadPacks();
        callDeployer(deployPacks);
    }

    private void callDeployer(List<ServiceDeployPack> deployPacks) {
        DefaultServiceDeployer deployer = defaultClusterDeployer(cli, paths);
        range(0, deployPacks.size()).forEach(i -> deployer.deploy(
                        deployPacks.get(i).service(),
                        deployPacks.get(i).deployment(),
                        deployPacks.get(i).files())
        );
    }

    private void callRunner(List<ServiceJobPack> jobPacks) {
        DefaultJobRunner runner = defaultJobRunner();
        range(0, jobPacks.size()).forEach(i -> runner.run(
                jobPacks.get(i).service(),
                jobPacks.get(i).job(),
                jobPacks.get(i).files())
        );
    }
}

package io.microconfig.osdf.api.implementations;

import io.microconfig.osdf.api.declarations.ManagementApi;
import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.service.deployment.pack.ServiceDeployPack;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.cluster.pod.PodDeleter.podDeleter;
import static io.microconfig.osdf.commands.DeployCommand.deployCommand;
import static io.microconfig.osdf.commands.decorators.DeleteBuggedDCDecorator.deleteFailed;
import static io.microconfig.osdf.service.deployment.pack.loader.DefaultServiceDeployPacksLoader.serviceLoader;
import static io.microconfig.osdf.service.deployment.tools.DeploymentRestarter.deploymentRestarter;
import static io.microconfig.osdf.service.loaders.filters.RequiredComponentsFilter.requiredComponentsFilter;
import static io.microconfig.utils.Logger.info;

@RequiredArgsConstructor
public class ManagementApiImpl implements ManagementApi {
    private final OSDFPaths paths;
    private final ClusterCLI cli;

    public static ManagementApi managementApi(OSDFPaths paths, ClusterCLI cli) {
        return new ManagementApiImpl(paths, cli);
    }

    @Override
    public void deploy(List<String> components, String mode) {
        cli.login();
        deleteFailed(cli, deployCommand(paths, cli)).deploy(components, mode);
    }

    @Override
    public void restart(List<String> components) {
        cli.login();
        List<ServiceDeployPack> deployPacks = serviceLoader(paths, requiredComponentsFilter(components), cli).loadPacks();
        deployPacks.forEach(pack -> deploymentRestarter().restart(pack.deployment(), pack.files()));
    }

    @Override
    public void stop(List<String> components) {
        cli.login();
        List<ServiceDeployPack> deployPacks = serviceLoader(paths, requiredComponentsFilter(components), cli).loadPacks();
        deployPacks.forEach(pack -> pack.deployment().scale(0));
    }

    @Override
    public void deletePod(String component, List<String> pods) {
        cli.login();
        podDeleter(paths, cli).delete(component, pods);
    }

    @Override
    public void clearDeployments(String version) {
        cli.login();
        List<ServiceDeployPack> deployPacks = serviceLoader(paths, cli).loadPacks();
        deployPacks.forEach(pack -> {
            String output = cli.execute("delete dc " + pack.service().name() + "." + version).getOutput();
            info(output);
        });
    }

    @Override
    public void delete(List<String> components) {
        serviceLoader(paths, requiredComponentsFilter(components), cli)
                .loadPacks()
                .forEach(pack -> {
                    pack.service().delete();
                    info("Deleted " + pack.service().name());
                });
    }
}

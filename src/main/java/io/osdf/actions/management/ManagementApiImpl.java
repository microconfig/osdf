package io.osdf.actions.management;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.settings.paths.OsdfPaths;
import io.osdf.core.service.core.deployment.pack.ServiceDeployPack;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.actions.management.deletepod.PodDeleter.podDeleter;
import static io.osdf.actions.management.deploy.DeployCommand.deployCommand;
import static io.osdf.actions.management.deploy.RunJobsCommand.runJobsCommand;
import static io.osdf.core.service.core.deployment.pack.loader.DefaultServiceDeployPacksLoader.serviceLoader;
import static io.osdf.actions.management.restart.DeploymentRestarter.deploymentRestarter;
import static io.osdf.core.service.local.loaders.filters.RequiredComponentsFilter.requiredComponentsFilter;
import static io.microconfig.utils.Logger.info;

@RequiredArgsConstructor
public class ManagementApiImpl implements ManagementApi {
    private final OsdfPaths paths;
    private final ClusterCli cli;

    public static ManagementApi managementApi(OsdfPaths paths, ClusterCli cli) {
        return new ManagementApiImpl(paths, cli);
    }

    @Override
    public void deploy(List<String> serviceNames, String mode, Boolean smart) {
        cli.login();
        if ("restricted".equals(mode) && smart) throw new OSDFException("Smart deploy is not possible for restricted deploy mode");
        runJobsCommand(paths, cli).run(serviceNames, smart);
        deployCommand(paths, cli).deploy(serviceNames, mode, smart);
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
    public void delete(List<String> components) {
        serviceLoader(paths, requiredComponentsFilter(components), cli)
                .loadPacks()
                .forEach(pack -> {
                    pack.service().delete();
                    info("Deleted " + pack.service().name());
                });
    }
}

package io.osdf.actions.management;

import io.osdf.actions.management.restart.DeploymentRestarter;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.actions.management.deletepod.PodDeleter.podDeleter;
import static io.osdf.actions.management.deploy.DeployCommand.deployCommand;
import static io.osdf.actions.management.deploy.RunJobsCommand.runJobsCommand;
import static io.osdf.actions.management.restart.DeploymentRestarter.deploymentRestarter;
import static io.osdf.core.application.ApplicationManager.applicationManager;
import static io.osdf.core.application.local.loaders.AllApplications.all;
import static io.osdf.core.application.local.loaders.ApplicationFilesLoaderImpl.activeRequiredAppsLoader;
import static io.osdf.core.application.service.ServiceApplicationMapper.service;

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
        deployCommand(paths, cli).deploy(serviceNames, smart);
    }

    @Override
    public void restart(List<String> components) {
        cli.login();
        DeploymentRestarter restarter = deploymentRestarter(cli);
        activeRequiredAppsLoader(paths, components)
                .load(service(cli))
                .forEach(restarter::restart);
    }

    @Override
    public void stop(List<String> components) {
        cli.login();
        activeRequiredAppsLoader(paths, components)
                .load(service(cli))
                .forEach(service -> service.deployment().scale(0));
    }

    @Override
    public void deletePod(String component, List<String> pods) {
        cli.login();
        podDeleter(paths, cli).delete(component, pods);
    }

    @Override
    public void clearAll(List<String> components) {
        activeRequiredAppsLoader(paths, components)
                .load(all())
                .forEach(app -> applicationManager(app.name(), cli).delete());
    }
}

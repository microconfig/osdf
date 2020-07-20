package io.osdf.actions.management;

import io.osdf.actions.management.restart.DeploymentRestarter;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.core.Application;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.utils.Logger.announce;
import static io.osdf.actions.management.deletepod.PodDeleter.podDeleter;
import static io.osdf.actions.management.deploy.AppsDeployCommand.deployCommand;
import static io.osdf.actions.management.restart.DeploymentRestarter.deploymentRestarter;
import static io.osdf.core.application.core.AllApplications.all;
import static io.osdf.core.application.core.files.loaders.ApplicationFilesLoaderImpl.activeRequiredAppsLoader;
import static io.osdf.core.application.service.ServiceApplicationMapper.service;
import static io.osdf.core.connection.cli.LoginCliProxy.loginCliProxy;

@RequiredArgsConstructor
public class ManagementApiImpl implements ManagementApi {
    private final OsdfPaths paths;
    private final ClusterCli cli;

    public static ManagementApi managementApi(OsdfPaths paths, ClusterCli cli) {
        return loginCliProxy(new ManagementApiImpl(paths, cli), cli);
    }

    @Override
    public void deploy(List<String> serviceNames, String mode, Boolean smart) {
        if ("restricted".equals(mode) && smart) throw new OSDFException("Smart deploy is not possible for restricted deploy mode");
        boolean ok = deployCommand(paths, cli).deploy(serviceNames, smart);
        announce(ok ? "OK" : "Some apps have failed");
    }

    @Override
    public void restart(List<String> components) {
        DeploymentRestarter restarter = deploymentRestarter(cli);
        activeRequiredAppsLoader(paths, components)
                .load(service(cli))
                .forEach(restarter::restart);
    }

    @Override
    public void stop(List<String> components) {
        activeRequiredAppsLoader(paths, components)
                .load(service(cli))
                .forEach(service -> service.deployment().scale(0));
    }

    @Override
    public void deletePod(String component, List<String> pods) {
        podDeleter(paths, cli).delete(component, pods);
    }

    @Override
    public void clearAll(List<String> components) {
        activeRequiredAppsLoader(paths, components)
                .load(all(cli))
                .forEach(Application::delete);
    }
}

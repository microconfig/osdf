package io.osdf.actions.management.deploy;

import io.osdf.actions.management.deploy.deployer.ServiceDeployerImpl;
import io.osdf.actions.management.deploy.smart.hash.ResourcesHashComputer;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.utils.Logger.announce;
import static io.osdf.actions.management.deploy.deployer.ServiceDeployerImpl.serviceDeployer;
import static io.osdf.actions.management.deploy.smart.UpToDateDeploymentFilter.upToDateDeploymentFilter;
import static io.osdf.actions.management.deploy.smart.hash.ResourcesHashComputer.resourcesHashComputer;
import static io.osdf.actions.management.deploy.smart.image.ImageTagReplacer.imageTagReplacer;
import static io.osdf.core.application.local.loaders.ApplicationFilesLoaderImpl.activeRequiredAppsLoader;
import static io.osdf.core.application.service.ServiceApplicationMapper.service;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class DeployCommand {
    private final OsdfPaths paths;
    private final ClusterCli cli;

    public static DeployCommand deployCommand(OsdfPaths paths, ClusterCli cli) {
        return new DeployCommand(paths, cli);
    }

    public boolean deploy(List<String> requiredServiceNames, boolean smart) {
        List<ServiceApplication> allServices = activeRequiredAppsLoader(paths, requiredServiceNames).load(service(cli));
        if (allServices.isEmpty()) return true;

        preprocessServices(allServices);
        List<ServiceApplication> servicesToDeploy = getServicesToDeploy(smart, allServices);
        if (servicesToDeploy.isEmpty()) return true;

        ServiceDeployerImpl deployer = serviceDeployer(cli);
        List<Boolean> result = servicesToDeploy.parallelStream()
                .map(deployer::deploy)
                .collect(toUnmodifiableList());
        return result.stream().allMatch(t -> t);
    }

    private List<ServiceApplication> getServicesToDeploy(boolean smart, List<ServiceApplication> allServices) {
        List<ServiceApplication> servicesToDeploy = smart ? upToDateDeploymentFilter(cli).filter(allServices) : allServices;
        if (servicesToDeploy.isEmpty()) {
            announce("All services are up-to-date");
        } else {
            announce("Deploying: " +
                    servicesToDeploy.stream()
                            .map(service -> service.files().name())
                            .collect(joining(" ")));
        }
        return servicesToDeploy;
    }

    private void preprocessServices(List<ServiceApplication> services) {
        replaceTags(services);
        insertHashes(services);
    }

    private void replaceTags(List<ServiceApplication> services) {
        services.forEach(service -> imageTagReplacer(paths).replaceFor(service.files()));
    }

    private void insertHashes(List<ServiceApplication> services) {
        ResourcesHashComputer resourcesHashComputer = resourcesHashComputer();
        services.forEach(service -> resourcesHashComputer.insertIn(service.files()));
    }

}

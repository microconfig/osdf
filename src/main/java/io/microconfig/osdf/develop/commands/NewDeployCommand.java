package io.microconfig.osdf.develop.commands;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.develop.component.ComponentDir;
import io.microconfig.osdf.develop.deployers.DefaultClusterDeployer;
import io.microconfig.osdf.develop.deployment.ClusterDeployment;
import io.microconfig.osdf.develop.service.DefaultClusterService;
import io.microconfig.osdf.develop.service.ServiceDeploymentMatcher;
import io.microconfig.osdf.develop.service.ServiceFiles;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.develop.component.AllMicroConfigComponentsLoader.componentsLoader;
import static io.microconfig.osdf.develop.deployers.DefaultClusterDeployer.defaultClusterDeployer;
import static io.microconfig.osdf.develop.service.DefaultClusterService.defaultClusterService;
import static io.microconfig.osdf.develop.service.DefaultServicesLoader.servicesLoader;
import static io.microconfig.osdf.develop.service.ServiceDeploymentMatcher.serviceDeploymentMatcher;
import static io.microconfig.utils.Logger.announce;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.IntStream.range;

@RequiredArgsConstructor
public class NewDeployCommand {
    private final OSDFPaths paths;
    private final ClusterCLI cli;

    public static NewDeployCommand deployCommand(OSDFPaths paths, ClusterCLI cli) {
        return new NewDeployCommand(paths, cli);
    }

    public void deploy(List<String> serviceNames, String mode) {
        announce("Starting deployment. Mode " + mode + " is ignored for now");
        List<ComponentDir> allComponents = componentsLoader(paths.componentsPath()).load();

        List<ServiceFiles> serviceFilesList = servicesLoader(serviceNames).load(allComponents);
        List<ClusterDeployment> deployments = deploymentsFromServiceFiles(serviceFilesList);
        List<DefaultClusterService> services = servicesFromDeployments(deployments);

        callDeployer(serviceFilesList, deployments, services);
    }

    private void callDeployer(List<ServiceFiles> serviceFilesList, List<ClusterDeployment> deployments, List<DefaultClusterService> services) {
        DefaultClusterDeployer deployer = defaultClusterDeployer(cli, paths);
        range(0, serviceFilesList.size())
                .forEach(i -> deployer.deploy(services.get(i), deployments.get(i), serviceFilesList.get(i)));
    }

    private List<ClusterDeployment> deploymentsFromServiceFiles(List<ServiceFiles> serviceFilesList) {
        ServiceDeploymentMatcher matcher = serviceDeploymentMatcher(cli);
        return serviceFilesList.stream()
                .map(matcher::match)
                .collect(toUnmodifiableList());
    }

    private List<DefaultClusterService> servicesFromDeployments(List<ClusterDeployment> deployments) {
        return deployments.stream()
                .map(deployment -> defaultClusterService(deployment.serviceName(), deployment.version(), cli))
                .collect(toUnmodifiableList());
    }
}

package io.microconfig.osdf.develop.service.deployment.pack.loader;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.develop.service.ClusterService;
import io.microconfig.osdf.develop.service.deployment.ServiceDeployment;
import io.microconfig.osdf.develop.service.deployment.matchers.ServiceDeploymentMatcher;
import io.microconfig.osdf.develop.service.deployment.pack.ServiceDeployPack;
import io.microconfig.osdf.develop.service.files.DefaultServiceFiles;
import io.microconfig.osdf.develop.service.files.ServiceFiles;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.develop.component.finder.MicroConfigComponentsFinder.componentsFinder;
import static io.microconfig.osdf.develop.component.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.develop.service.DefaultClusterService.defaultClusterService;
import static io.microconfig.osdf.develop.service.deployment.matchers.ServiceDeploymentMatcher.serviceDeploymentMatcher;
import static io.microconfig.osdf.develop.service.deployment.pack.DefaultServiceDeployPack.serviceDeployPack;
import static io.microconfig.osdf.develop.service.loaders.DefaultServiceFilesLoader.servicesLoader;
import static java.nio.file.Files.exists;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.IntStream.range;

@RequiredArgsConstructor
public class DefaultServiceDeployPacksLoader implements ServiceDeployPacksLoader {
    private final OSDFPaths paths;
    private final List<String> requiredServicesNames;
    private final ClusterCLI cli;

    public static DefaultServiceDeployPacksLoader defaultServiceDeployPacksLoader(OSDFPaths paths, List<String> requiredServicesNames,
                                                                                  ClusterCLI cli) {
        return new DefaultServiceDeployPacksLoader(paths, requiredServicesNames, cli);
    }

    public static DefaultServiceDeployPacksLoader defaultServiceDeployPacksLoader(OSDFPaths paths, ClusterCLI cli) {
        return new DefaultServiceDeployPacksLoader(paths, null, cli);
    }

    @Override
    public List<ServiceDeployPack> loadPacks() {
        List<ServiceFiles> serviceFilesList = servicesLoader(paths, requiredServicesNames, this::isDeploymentService).load();
        List<ServiceDeployment> deployments = deploymentsFromServiceFiles(serviceFilesList);
        List<ClusterService> services = servicesFromDeployments(deployments);

        return range(0, services.size())
                .mapToObj(i -> serviceDeployPack(serviceFilesList.get(i), deployments.get(i), services.get(i)))
                .collect(toUnmodifiableList());
    }

    @Override
    public ServiceDeployPack loadByName(String name) {
        ServiceFiles serviceFiles = componentsLoader().loadOne(name, componentsFinder(paths.componentsPath()), DefaultServiceFiles::serviceFiles);
        ServiceDeployment deployment = serviceDeploymentMatcher(cli).match(serviceFiles);
        ClusterService service = defaultClusterService(name, deployment.version(), cli);

        return serviceDeployPack(serviceFiles, deployment, service);
    }

    private List<ServiceDeployment> deploymentsFromServiceFiles(List<ServiceFiles> serviceFilesList) {
        ServiceDeploymentMatcher matcher = serviceDeploymentMatcher(cli);
        return serviceFilesList.stream()
                .map(matcher::match)
                .collect(toUnmodifiableList());
    }

    private List<ClusterService> servicesFromDeployments(List<ServiceDeployment> deployments) {
        return deployments.stream()
                .map(deployment -> defaultClusterService(deployment.serviceName(), deployment.version(), cli))
                .collect(toUnmodifiableList());
    }

    private boolean isDeploymentService(ServiceFiles files) {
        return exists(files.getPath("resources/deployment.yaml"));
    }
}

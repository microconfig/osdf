package io.microconfig.osdf.service.deployment.pack.loader;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.component.ComponentDir;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.service.ClusterService;
import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.deployment.matchers.ServiceDeploymentMatcher;
import io.microconfig.osdf.service.deployment.pack.ServiceDeployPack;
import io.microconfig.osdf.service.files.DefaultServiceFiles;
import io.microconfig.osdf.service.files.ServiceFiles;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Predicate;

import static io.microconfig.osdf.component.finder.MicroConfigComponentsFinder.componentsFinder;
import static io.microconfig.osdf.component.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.service.deployment.matchers.ServiceDeploymentMatcher.serviceDeploymentMatcher;
import static io.microconfig.osdf.service.deployment.pack.DefaultServiceDeployPack.serviceDeployPack;
import static io.microconfig.osdf.service.loaders.DefaultServiceFilesLoader.activeServicesLoader;
import static io.microconfig.osdf.service.matchers.ClusterServiceMatcher.matcher;
import static java.nio.file.Files.exists;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.IntStream.range;

@RequiredArgsConstructor
public class DefaultServiceDeployPacksLoader implements ServiceDeployPacksLoader {
    private final OSDFPaths paths;
    private final Predicate<ComponentDir> dirFilter;
    private final ClusterCLI cli;

    public static DefaultServiceDeployPacksLoader serviceLoader(OSDFPaths paths, Predicate<ComponentDir> dirFilter,
                                                                ClusterCLI cli) {
        return new DefaultServiceDeployPacksLoader(paths, dirFilter, cli);
    }

    public static DefaultServiceDeployPacksLoader serviceLoader(OSDFPaths paths, ClusterCLI cli) {
        return new DefaultServiceDeployPacksLoader(paths, dir -> true, cli);
    }

    @Override
    public List<ServiceDeployPack> loadPacks() {
        List<ServiceFiles> serviceFilesList = activeServicesLoader(paths)
                .withDirFilter(dirFilter)
                .withServiceFilter(this::isDeploymentService)
                .load();
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
        ClusterService service = matcher(cli).match(deployment);

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
                .map(deployment -> matcher(cli).match(deployment))
                .collect(toUnmodifiableList());
    }

    private boolean isDeploymentService(ServiceFiles files) {
        return exists(files.getPath("resources/deployment.yaml"));
    }
}

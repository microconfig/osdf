package io.osdf.core.service.core.deployment.pack.loader;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.local.component.ComponentDir;
import io.osdf.settings.paths.OsdfPaths;
import io.osdf.core.service.cluster.ClusterService;
import io.osdf.core.service.core.deployment.ServiceDeployment;
import io.osdf.core.service.core.deployment.ServiceDeploymentMatcher;
import io.osdf.core.service.core.deployment.pack.ServiceDeployPack;
import io.osdf.core.service.local.DefaultServiceFiles;
import io.osdf.core.service.local.ServiceFiles;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Predicate;

import static io.osdf.core.local.component.finder.MicroConfigComponentsFinder.componentsFinder;
import static io.osdf.core.local.component.loader.ComponentsLoaderImpl.componentsLoader;
import static io.osdf.core.service.core.deployment.ServiceDeploymentMatcher.serviceDeploymentMatcher;
import static io.osdf.core.service.core.deployment.pack.DefaultServiceDeployPack.serviceDeployPack;
import static io.osdf.core.service.local.loaders.DefaultServiceFilesLoader.activeServicesLoader;
import static io.osdf.core.service.cluster.ClusterServiceMatcher.matcher;
import static java.nio.file.Files.exists;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.IntStream.range;

@RequiredArgsConstructor
public class DefaultServiceDeployPacksLoader implements ServiceDeployPacksLoader {
    private final OsdfPaths paths;
    private final Predicate<ComponentDir> dirFilter;
    private final ClusterCli cli;

    public static DefaultServiceDeployPacksLoader serviceLoader(OsdfPaths paths, Predicate<ComponentDir> dirFilter,
                                                                ClusterCli cli) {
        return new DefaultServiceDeployPacksLoader(paths, dirFilter, cli);
    }

    public static DefaultServiceDeployPacksLoader serviceLoader(OsdfPaths paths, ClusterCli cli) {
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

package io.microconfig.osdf.service.loaders;

import io.microconfig.osdf.component.ComponentDir;
import io.osdf.settings.paths.OsdfPaths;
import io.microconfig.osdf.service.files.DefaultServiceFiles;
import io.microconfig.osdf.service.files.ServiceFiles;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static io.microconfig.osdf.component.finder.MicroConfigComponentsFinder.componentsFinder;
import static io.microconfig.osdf.component.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.service.loaders.filters.ActiveComponentsFilter.activeComponentsFilter;
import static java.nio.file.Files.exists;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class DefaultServiceFilesLoader implements ServiceFilesLoader {
    private final OsdfPaths paths;
    private final List<Predicate<ComponentDir>> dirFilters = new ArrayList<>();
    private final List<Predicate<ServiceFiles>> serviceFilters = new ArrayList<>();

    public static DefaultServiceFilesLoader servicesLoader(OsdfPaths paths) {
        return new DefaultServiceFilesLoader(paths);
    }

    public static DefaultServiceFilesLoader activeServicesLoader(OsdfPaths paths) {
        return new DefaultServiceFilesLoader(paths).withDirFilter(activeComponentsFilter(paths));
    }

    @Override
    public List<ServiceFiles> load() {
        return componentsLoader()
                .load(componentsFinder(paths.componentsPath()), this::dirFilter, DefaultServiceFiles::serviceFiles)
                .stream()
                .filter(this::serviceFilter)
                .collect(toUnmodifiableList());
    }

    public DefaultServiceFilesLoader withDirFilter(Predicate<ComponentDir> filter) {
        dirFilters.add(filter);
        return this;
    }

    public DefaultServiceFilesLoader withServiceFilter(Predicate<ServiceFiles> filter) {
        serviceFilters.add(filter);
        return this;
    }

    private boolean dirFilter(ComponentDir componentDir) {
        return isService(componentDir) && dirFilters.stream().allMatch(filter -> filter.test(componentDir));
    }

    private boolean serviceFilter(ServiceFiles serviceFiles) {
        return serviceFilters.stream().allMatch(filter -> filter.test(serviceFiles));
    }

    private boolean isService(ComponentDir componentDir) {
        return exists(componentDir.getPath("resources")) || exists(componentDir.getPath("openshift"));
    }
}

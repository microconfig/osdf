package io.microconfig.osdf.service.loaders;

import io.microconfig.osdf.component.ComponentDir;
import io.microconfig.osdf.service.files.DefaultServiceFiles;
import io.microconfig.osdf.service.files.ServiceFiles;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Predicate;

import static io.microconfig.osdf.configs.ActiveComponents.activeComponents;
import static io.microconfig.osdf.component.finder.MicroConfigComponentsFinder.componentsFinder;
import static io.microconfig.osdf.component.loader.ComponentsLoaderImpl.componentsLoader;
import static java.nio.file.Files.exists;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class DefaultServiceFilesLoader implements ServiceFilesLoader {
    private final OSDFPaths paths;
    private final List<String> requiredServicesNames;
    private final List<String> activeComponentNames;
    private final Predicate<ServiceFiles> filter;

    public static DefaultServiceFilesLoader servicesLoader(OSDFPaths paths,
                                                           List<String> requiredServicesNames,
                                                           Predicate<ServiceFiles> filter) {
        return new DefaultServiceFilesLoader(paths, requiredServicesNames, activeComponents(paths).get(), filter);
    }

    @Override
    public List<ServiceFiles> load() {
        return componentsLoader()
                .load(componentsFinder(paths.componentsPath()), this::check, DefaultServiceFiles::serviceFiles)
                .stream()
                .filter(filter)
                .collect(toUnmodifiableList());
    }

    private boolean check(ComponentDir componentDir) {
        return isService(componentDir) && isRequired(componentDir) && activeComponentNames.contains(componentDir.name());
    }

    private boolean isService(ComponentDir componentDir) {
        return exists(componentDir.getPath("resources"))
                || exists(componentDir.getPath("openshift"))
                || exists(componentDir.getPath("common"));
    }

    private boolean isRequired(ComponentDir componentDir) {
        if (requiredServicesNames == null || requiredServicesNames.isEmpty()) return true;
        return requiredServicesNames.contains(componentDir.name());
    }
}

package io.osdf.core.application.core.files.loaders;

import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.application.core.files.ApplicationFilesImpl;
import io.osdf.core.local.component.ComponentDir;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static io.osdf.core.application.core.files.loaders.filters.ActiveComponentsFilter.activeComponentsFilter;
import static io.osdf.core.application.core.files.loaders.filters.AppFilter.isApp;
import static io.osdf.core.application.core.files.loaders.filters.HiddenComponentsFilter.hiddenComponentsFilter;
import static io.osdf.core.application.core.files.loaders.filters.RequiredComponentsFilter.requiredComponentsFilter;
import static io.osdf.core.local.component.finder.MicroConfigComponentsFinder.componentsFinder;
import static io.osdf.core.local.component.loader.ComponentsLoaderImpl.componentsLoader;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class ApplicationFilesLoaderImpl implements ApplicationFilesLoader {
    private final OsdfPaths paths;
    private final List<Predicate<ComponentDir>> dirFilters = new ArrayList<>();
    private final List<Predicate<ApplicationFiles>> appFilters = new ArrayList<>();

    public static ApplicationFilesLoaderImpl appLoader(OsdfPaths paths) {
        return new ApplicationFilesLoaderImpl(paths);
    }

    public static ApplicationFilesLoaderImpl activeRequiredAppsLoader(OsdfPaths paths, List<String> requiredNames) {
        return new ApplicationFilesLoaderImpl(paths)
                .withDirFilter(activeComponentsFilter(paths))
                .withDirFilter(requiredComponentsFilter(requiredNames))
                .withAppFilter(hiddenComponentsFilter());
    }

    @Override
    public List<ApplicationFiles> load() {
        return componentsLoader()
                .load(componentsFinder(paths.componentsPath()), this::dirFilter)
                .stream()
                .map(ApplicationFilesImpl::applicationFiles)
                .filter(this::serviceFilter)
                .collect(toUnmodifiableList());
    }

    @Override
    public <T> List<T> load(ApplicationMapper<T> appMapper) {
        return componentsLoader()
                .load(componentsFinder(paths.componentsPath()), this::dirFilter).stream()
                .map(ApplicationFilesImpl::applicationFiles)
                .filter(this::serviceFilter)
                .filter(appMapper::check)
                .map(appMapper::map)
                .collect(toUnmodifiableList());
    }

    public ApplicationFilesLoaderImpl withDirFilter(Predicate<ComponentDir> filter) {
        dirFilters.add(filter);
        return this;
    }

    public ApplicationFilesLoaderImpl withAppFilter(Predicate<ApplicationFiles> filter) {
        appFilters.add(filter);
        return this;
    }

    private boolean dirFilter(ComponentDir componentDir) {
        return isApp().test(componentDir) && dirFilters.stream().allMatch(filter -> filter.test(componentDir));
    }

    private boolean serviceFilter(ApplicationFiles applicationFiles) {
        return appFilters.stream().allMatch(filter -> filter.test(applicationFiles));
    }
}

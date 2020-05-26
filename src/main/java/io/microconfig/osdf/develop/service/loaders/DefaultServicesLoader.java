package io.microconfig.osdf.develop.service.loaders;

import io.microconfig.osdf.develop.component.ComponentDir;
import io.microconfig.osdf.develop.service.files.DefaultServiceFiles;
import io.microconfig.osdf.develop.service.files.ServiceFiles;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static java.nio.file.Files.exists;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class DefaultServicesLoader implements ServicesLoader {
    private final List<String> requiredServicesNames;

    public static DefaultServicesLoader servicesLoader(List<String> requiredServicesNames) {
        return new DefaultServicesLoader(requiredServicesNames);
    }

    @Override
    public List<ServiceFiles> load(List<? extends ComponentDir> componentDirs) {
        return componentDirs.stream()
                .filter(this::isRequired)
                .filter(this::isService)
                .map(DefaultServiceFiles::serviceFiles)
                .collect(toUnmodifiableList());
    }

    private boolean isService(ComponentDir componentDir) {
        return exists(componentDir.getPath("resources")) || exists(componentDir.getPath("openshift"));
    }

    private boolean isRequired(ComponentDir componentDir) {
        if (requiredServicesNames == null || requiredServicesNames.isEmpty()) return true;
        return requiredServicesNames.contains(componentDir.name());
    }
}

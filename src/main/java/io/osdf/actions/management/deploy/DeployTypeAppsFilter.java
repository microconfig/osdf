package io.osdf.actions.management.deploy;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.core.Application;
import io.osdf.core.local.component.ComponentDir;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static io.osdf.common.yaml.YamlObject.yaml;
import static io.osdf.core.local.component.finder.MicroConfigComponentsFinder.componentsFinder;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class DeployTypeAppsFilter {
    private final OsdfPaths paths;

    public static DeployTypeAppsFilter deployTypeAppsFilter(OsdfPaths paths) {
        return new DeployTypeAppsFilter(paths);
    }

    public List<Application> filter(List<Application> apps, String type) {
        Map<String, Object> typeSettings = typeSettings(type);
        if (typeSettings == null) return apps;

        List<String> includes = getListOrEmpty(typeSettings, "include");
        List<String> excludes = getListOrEmpty(typeSettings, "exclude");
        return apps.stream()
                .filter(app -> includes.isEmpty() || includes.contains(app.name()))
                .filter(app -> !excludes.contains(app.name()))
                .collect(toUnmodifiableList());
    }

    private Map<String, Object> typeSettings(String type) {
        ComponentDir deploySettingsComponent = deploySettingsComponent();
        if (deploySettingsComponent == null) return null;

        return yaml(deploySettingsComponent.getPath("application.yaml"))
                .get("deploy.types." + type);
    }

    private ComponentDir deploySettingsComponent() {
        try {
            return componentsFinder(paths.componentsPath())
                    .findByName("deploy-settings");
        } catch (OSDFException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> getListOrEmpty(Map<String, Object> typeSettings, String include) {
        List<String> list = (List<String>) typeSettings.get(include);
        return list != null ? list : emptyList();
    }
}

package io.microconfig.osdf.configs;

import io.microconfig.core.environments.Component;
import io.microconfig.core.environments.Components;
import io.microconfig.core.environments.Environment;
import io.microconfig.osdf.exceptions.MicroConfigException;
import io.osdf.settings.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.core.Microconfig.searchConfigsIn;
import static io.microconfig.osdf.settings.SettingsFile.settingsFile;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class MicroConfigComponents {
    private final OSDFPaths paths;
    private final String env;
    private final String group;

    public static MicroConfigComponents microConfigComponents(OSDFPaths paths) {
        ConfigsSettings settings = settingsFile(ConfigsSettings.class, paths.settings().configs()).getSettings();
        String group = settings.getGroup();
        String env = settings.getEnv();
        return new MicroConfigComponents(paths, env, group);
    }

    public List<String> active() {
        return forGroup(group);
    }

    public List<String> forGroup(String group) {
        try {
            Environment environment = searchConfigsIn(paths.configsPath().toFile()).inEnvironment(env);
            if (group == null || group.equals("ALL")) {
                return toComponentNames(environment.getAllComponents());
            }
            return toComponentNames(environment.getGroupWithName(group).getComponents());
        } catch (RuntimeException e) {
            throw new MicroConfigException(e);
        }
    }

    private List<String> toComponentNames(Components components) {
        return components
                .asList()
                .stream()
                .map(Component::getName)
                .collect(toUnmodifiableList());
    }
}

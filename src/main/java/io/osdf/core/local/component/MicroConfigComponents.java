package io.osdf.core.local.component;

import io.microconfig.core.environments.Component;
import io.microconfig.core.environments.Components;
import io.microconfig.core.environments.Environment;
import io.osdf.common.exceptions.MicroConfigException;
import io.osdf.core.local.configs.ConfigsSettings;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.core.Microconfig.searchConfigsIn;
import static io.osdf.common.SettingsFile.settingsFile;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class MicroConfigComponents {
    private final OsdfPaths paths;
    private final String env;
    private final String group;

    public static MicroConfigComponents microConfigComponents(OsdfPaths paths) {
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

package io.microconfig.osdf.microconfig;

import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.microconfig.files.MicroConfigFilesState;
import io.microconfig.osdf.state.OSDFState;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

import static io.microconfig.core.Microconfig.searchConfigsIn;
import static io.microconfig.core.configtypes.ConfigTypeFilters.eachConfigType;
import static io.microconfig.core.properties.serializers.PropertySerializers.toFileIn;
import static io.microconfig.core.properties.serializers.PropertySerializers.withConfigDiff;
import static io.microconfig.core.properties.templates.CopyTemplatesService.resolveTemplatesBy;
import static io.microconfig.osdf.microconfig.files.MicroConfigFilesState.of;
import static io.microconfig.osdf.utils.FileUtils.createDirectoryIfNotExists;
import static java.nio.file.Files.exists;
import static java.util.Collections.emptyList;
import static java.util.List.of;

@RequiredArgsConstructor
public class MicroConfig {
    private final String env;
    private final String group;
    private final List<String> components;
    private final OSDFPaths paths;

    public static MicroConfig microConfig(OSDFState state, OSDFPaths paths) {
        return new MicroConfig(state.getEnv(), state.getGroup(), state.getComponents(), paths);
    }

    public void generateConfigs() {
        if (!exists(paths.componentsPath())) {
            generateConfigs(env, group, components, paths.configPath(), paths.componentsPath());
        } else {
            generateConfigsAndClearUnchanged(env, group, components, paths.configPath(), paths.componentsPath());
        }
    }

    private void generateConfigs(String env, String group, List<String> components, Path from, Path to) {
        searchConfigsIn(from.toFile())
                .withDestinationDir(to.toFile())
                .inEnvironment(env)
                .findComponentsFrom(group == null ? emptyList() : of(group), components == null ? emptyList() : components)
                .getPropertiesFor(eachConfigType())
                .resolveBy(searchConfigsIn(from.toFile()).withDestinationDir(to.toFile()).resolver())
                .forEachComponent(resolveTemplatesBy(searchConfigsIn(from.toFile()).withDestinationDir(to.toFile()).resolver()))
                .save(toFileIn(to.toFile(), withConfigDiff()));
        createDirectoryIfNotExists(to);
    }

    private void generateConfigsAndClearUnchanged(String env, String group, List<String> components, Path from, Path to) {
        MicroConfigFilesState oldState = of(paths.componentsPath());
        generateConfigs(env, group, components, from, to);
        MicroConfigFilesState newState = of(paths.componentsPath());
        oldState.clearUnchanged(newState);
    }
}

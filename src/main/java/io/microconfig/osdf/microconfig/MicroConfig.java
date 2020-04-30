package io.microconfig.osdf.microconfig;

import io.microconfig.osdf.microconfig.files.MicroConfigFilesState;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.microconfig.core.Microconfig.searchConfigsIn;
import static io.microconfig.core.configtypes.ConfigTypeFilters.eachConfigType;
import static io.microconfig.core.properties.serializers.PropertySerializers.toFileIn;
import static io.microconfig.core.properties.serializers.PropertySerializers.withConfigDiff;
import static io.microconfig.core.properties.templates.CopyTemplatesService.resolveTemplatesBy;
import static io.microconfig.osdf.microconfig.files.MicroConfigFilesState.of;
import static io.microconfig.osdf.utils.FileUtils.createDirectoryIfNotExists;
import static java.nio.file.Files.exists;
import static java.util.Collections.emptyList;

@RequiredArgsConstructor
public class MicroConfig {
    private final String env;
    private final OSDFPaths paths;

    public static MicroConfig microConfig(String env, OSDFPaths paths) {
        return new MicroConfig(env, paths);
    }

    public void generateConfigs() {
        if (!exists(paths.componentsPath())) {
            generateConfigs(env, paths.configsPath(), paths.componentsPath());
        } else {
            generateConfigsAndClearUnchanged(env, paths.configsPath(), paths.componentsPath());
        }
    }

    private void generateConfigs(String env, Path from, Path to) {
        searchConfigsIn(from.toFile())
                .withDestinationDir(to.toFile())
                .inEnvironment(env)
                .findComponentsFrom(emptyList(), emptyList())
                .getPropertiesFor(eachConfigType())
                .resolveBy(searchConfigsIn(from.toFile()).withDestinationDir(to.toFile()).resolver())
                .forEachComponent(resolveTemplatesBy(searchConfigsIn(from.toFile()).withDestinationDir(to.toFile()).resolver()))
                .save(toFileIn(to.toFile(), withConfigDiff()));
        createDirectoryIfNotExists(to);
    }

    private void generateConfigsAndClearUnchanged(String env, Path from, Path to) {
        MicroConfigFilesState oldState = of(paths.componentsPath());
        generateConfigs(env, from, to);
        MicroConfigFilesState newState = of(paths.componentsPath());
        oldState.clearUnchanged(newState);
    }
}

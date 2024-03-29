package io.osdf.core.local.microconfig;

import io.osdf.common.exceptions.MicroConfigException;
import io.osdf.core.local.microconfig.state.MicroConfigFilesState;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

import static io.microconfig.core.Microconfig.searchConfigsIn;
import static io.microconfig.core.configtypes.ConfigTypeFilters.eachConfigType;
import static io.microconfig.core.properties.serializers.PropertySerializers.toFileIn;
import static io.microconfig.core.properties.serializers.PropertySerializers.withConfigDiff;
import static io.microconfig.core.properties.templates.TemplatesService.resolveTemplatesBy;
import static io.osdf.common.utils.CommandLineExecutor.execute;
import static io.osdf.common.utils.FileUtils.createDirectoryIfNotExists;
import static io.osdf.common.utils.FileUtils.delete;
import static io.osdf.core.local.microconfig.state.MicroConfigFilesState.of;
import static java.nio.file.Files.exists;
import static java.nio.file.Path.of;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@RequiredArgsConstructor
public class MicroConfig {
    private final String env;
    private final OsdfPaths paths;

    public static MicroConfig microConfig(String env, OsdfPaths paths) {
        return new MicroConfig(env, paths);
    }

    public void generateConfigs(List<String> components) {
        if (!exists(paths.componentsPath())) {
            generateConfigs(env, components, paths.configsPath(), paths.componentsPath());
        } else {
            generateConfigsAndClearUnchanged(env, components, paths.configsPath(), paths.componentsPath());
        }
    }

    public void generateSingleComponent(String component) {
        generateConfigs(env, singletonList(component), paths.configsPath(), of("/tmp/microconfig"));

        delete(of(paths.componentsPath() + "/" + component));
        execute("mv /tmp/microconfig/" + component + " " + paths.componentsPath() + "/" + component);
    }

    private void generateConfigs(String env, List<String> components, Path from, Path to) {
        try {
            searchConfigsIn(from.toFile())
                    .withDestinationDir(to.toFile())
                    .inEnvironment(env)
                    .findComponentsFrom(emptyList(), components)
                    .getPropertiesFor(eachConfigType())
                    .resolveBy(searchConfigsIn(from.toFile()).withDestinationDir(to.toFile()).resolver())
                    .forEachComponent(resolveTemplatesBy(searchConfigsIn(from.toFile()).withDestinationDir(to.toFile()).resolver()))
                    .save(toFileIn(to.toFile(), withConfigDiff()));
        } catch (RuntimeException e) {
            throw new MicroConfigException(e);
        }
        createDirectoryIfNotExists(to);
    }

    private void generateConfigsAndClearUnchanged(String env, List<String> components, Path from, Path to) {
        MicroConfigFilesState oldState = of(paths.componentsPath());
        generateConfigs(env, components, from, to);
        MicroConfigFilesState newState = of(paths.componentsPath());
        oldState.clearUnchanged(newState);
    }
}

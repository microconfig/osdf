package io.microconfig.osdf.microconfig;

import io.microconfig.commands.ComponentsToProcess;
import io.microconfig.commands.ConfigCommand;
import io.microconfig.factory.BuildConfigCommandFactory;
import io.microconfig.factory.configtypes.ConfigTypeFileProvider;
import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.microconfig.files.MicroConfigFilesState;
import io.microconfig.osdf.state.OSDFState;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

import static io.microconfig.factory.configtypes.CompositeConfigTypeProvider.composite;
import static io.microconfig.factory.configtypes.StandardConfigTypes.asProvider;
import static io.microconfig.osdf.microconfig.files.MicroConfigFilesState.of;
import static io.microconfig.osdf.utils.FileUtils.createDirectoryIfNotExists;
import static java.nio.file.Files.exists;
import static java.util.Collections.emptyList;

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
        BuildConfigCommandFactory factory = new BuildConfigCommandFactory(composite(new ConfigTypeFileProvider(), asProvider()));
        ConfigCommand command = factory.newCommand(from.toFile(), to.toFile());
        command.execute(new ComponentsToProcess(env, group, components == null ? emptyList() : components));
        createDirectoryIfNotExists(to);
    }

    private void generateConfigsAndClearUnchanged(String env, String group, List<String> components, Path from, Path to) {
        MicroConfigFilesState oldState = of(paths.componentsPath());
        generateConfigs(env, group, components, from, to);
        MicroConfigFilesState newState = of(paths.componentsPath());
        oldState.clearUnchanged(newState);
    }
}

package io.microconfig.osdf.commands;

import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.nexus.NexusArtifact;
import io.microconfig.osdf.state.ConfigSource;
import io.microconfig.osdf.state.Credentials;
import io.microconfig.osdf.state.OSDFState;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

import static io.microconfig.osdf.configfetcher.ConfigFetcher.fetcher;
import static io.microconfig.osdf.install.AutoCompleteInstaller.autoCompleteInstaller;
import static io.microconfig.osdf.microconfig.MicroConfig.microConfig;
import static io.microconfig.osdf.microconfig.properties.PropertySetter.propertySetter;
import static io.microconfig.osdf.state.OSDFState.fromFile;
import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static io.microconfig.utils.Logger.announce;

@RequiredArgsConstructor
public class InitCommand {
    private final OSDFPaths paths;

    @SuppressWarnings({"DuplicatedCode"})
    public void run(String gitUrl, String nexusUrl, NexusArtifact configsNexusArtifact, Path localConfigs, ConfigSource configSource, Credentials openShiftCredentials, Credentials nexusCredentials,
                    String env, String configVersion, String group, String projVersion, List<String> components) {

        OSDFState state = fromFile(paths.stateSavePath());
        state.setIfNotNull(OSDFState::setGitUrl, gitUrl);
        state.setIfNotNull(OSDFState::setNexusUrl, nexusUrl);
        state.setIfNotNull(OSDFState::setConfigsNexusArtifact, configsNexusArtifact);
        state.setIfNotNull(OSDFState::setLocalConfigs, localConfigs != null ? localConfigs.toString() : null);
        state.setIfNotNull(OSDFState::setConfigSource, configSource);
        state.setIfNotNull(OSDFState::setOpenShiftCredentials, openShiftCredentials);
        state.setIfNotNull(OSDFState::setNexusCredentials, nexusCredentials);
        state.setIfNotNull(OSDFState::setEnv, env);
        state.setIfNotNull(OSDFState::setConfigVersion, configVersion);
        state.setIfNotNull(OSDFState::setGroup, group);
        state.setIfNotNull(OSDFState::setProjectVersion, projVersion);
        state.setIfNotNull(OSDFState::setComponents, components);
        state.save(paths.stateSavePath());

        if (!state.check()) return;
        fetchAndBuild(state);
        announce("Successfully initialized osdf");
    }

    private void fetchAndBuild(OSDFState state) {
        deleteFolders();
        fetcher(state, paths.configsDownloadPath()).fetchConfigs();
        propertySetter().setIfNecessary(paths.projectVersionPath(), "project.version", state.getProjectVersion());
        propertySetter().setIfNecessary(paths.configVersionPath(), "config.version", state.getConfigVersion());
        microConfig(state, paths).generateConfigs();
        autoCompleteInstaller(paths.componentsPath()).installAutoComplete(true);
    }

    private void deleteFolders() {
        execute("rm -rf " + paths.configsDownloadPath());
    }
}

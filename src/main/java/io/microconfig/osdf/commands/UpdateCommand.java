package io.microconfig.osdf.commands;

import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.state.OSDFState;
import io.microconfig.osdf.state.OSDFVersion;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static io.microconfig.osdf.install.OSDFInstaller.osdfInstaller;
import static io.microconfig.osdf.install.OSDFSource.REMOTE;
import static io.microconfig.osdf.microconfig.properties.PropertyGetter.propertyGetter;
import static io.microconfig.osdf.state.OSDFState.fromFile;
import static io.microconfig.osdf.state.OSDFVersion.fromConfigs;
import static io.microconfig.osdf.state.OSDFVersion.fromState;
import static io.microconfig.osdf.utils.ProcessUtil.startAndWait;
import static io.microconfig.utils.Logger.*;
import static java.lang.System.exit;
import static java.util.List.of;

@RequiredArgsConstructor
public class UpdateCommand {
    private final OSDFPaths paths;
    private final OSDFVersion stateVersion;
    private final OSDFVersion configsVersion;

    public static UpdateCommand updateCommand(OSDFPaths paths) {
        OSDFState state = fromFile(paths.stateSavePath());
        OSDFVersion stateVersion = fromState(state);
        OSDFVersion configsVersion = fromConfigs(propertyGetter(state.getEnv(), paths.configPath()));
        return new UpdateCommand(paths, stateVersion, configsVersion);
    }

    public void update() {
        if (!stateVersion.olderThan(configsVersion)) {
            announce("Current version is up-to-date with version from configs");
            return;
        }
        osdfInstaller(paths).install(stateVersion, configsVersion, REMOTE);
        announce("Updated to " + configsVersion);
    }

    public void tryPatchUpdateAndRestart(String[] args) {
        try {
            if (!stateVersion.olderThan(configsVersion)) return;
            if (stateVersion.hasOlderMinorThan(configsVersion)) {
                warn("Current version is significantly older than version in configs. Will not auto-update. Update using <osdf update> command");
                return;
            }

            osdfInstaller(paths).install(stateVersion, configsVersion, REMOTE, false);
            announce("Updated to " + configsVersion);
        } catch (Exception e) {
            warn("Can't auto-update. Try manual update or install new version");
            return;
        }
        restart(args);
    }


    private void restart(String[] args) {
        List<String> processArgs = new ArrayList<>(of("osdf"));
        processArgs.addAll(of(args));

        info("Restarting...\n");
        exit(startAndWait(new ProcessBuilder(processArgs).inheritIO()));
    }
}

package io.microconfig.osdf.commands;

import io.osdf.settings.paths.OSDFPaths;
import io.microconfig.osdf.state.OSDFVersion;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static io.microconfig.osdf.install.OSDFInstaller.osdfInstaller;
import static io.microconfig.osdf.install.jarinstaller.RemoteJarInstaller.jarInstaller;
import static io.microconfig.osdf.microconfig.properties.PropertyGetter.propertyGetter;
import static io.microconfig.osdf.state.OSDFVersion.fromConfigs;
import static io.microconfig.osdf.state.OSDFVersion.fromSettings;
import static io.microconfig.osdf.utils.ProcessUtil.startAndWait;
import static io.microconfig.utils.Logger.info;
import static io.microconfig.utils.Logger.warn;
import static java.lang.System.exit;
import static java.util.List.of;

@RequiredArgsConstructor
public class UpdateCommand {
    private final OSDFPaths paths;

    public static UpdateCommand updateCommand(OSDFPaths paths) {
        return new UpdateCommand(paths);
    }

    public void update(OSDFVersion version) {
        osdfInstaller(paths, jarInstaller(version, paths), false, false).install();
    }

    public void tryAutoUpdateAndRestart(String[] args) {
        try {
            OSDFVersion configsVersion = fromConfigs(propertyGetter(paths));
            OSDFVersion currentVersion = fromSettings(paths.settings().osdf());
            if (!currentVersion.olderThan(configsVersion)) return;
            update(configsVersion);
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

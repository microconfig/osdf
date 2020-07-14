package io.osdf.actions.system.update;

import io.osdf.settings.paths.OsdfPaths;
import io.osdf.settings.version.OsdfVersion;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static io.microconfig.utils.Logger.info;
import static io.microconfig.utils.Logger.warn;
import static io.osdf.actions.system.install.OsdfInstaller.osdfInstaller;
import static io.osdf.actions.system.install.jarinstaller.RemoteJarInstaller.jarInstaller;
import static io.osdf.common.utils.ProcessUtil.startAndWait;
import static io.osdf.settings.version.OsdfVersion.fromConfigs;
import static io.osdf.settings.version.OsdfVersion.fromSettings;
import static java.lang.System.exit;
import static java.util.List.of;

@RequiredArgsConstructor
public class UpdateCommand {
    private final OsdfPaths paths;

    public static UpdateCommand updateCommand(OsdfPaths paths) {
        return new UpdateCommand(paths);
    }

    public void update(OsdfVersion version) {
        osdfInstaller(paths, jarInstaller(version, paths), false, false).install();
    }

    public void tryAutoUpdateAndRestart(String[] args) {
        try {
            OsdfVersion configsVersion = fromConfigs(paths);
            OsdfVersion currentVersion = fromSettings(paths.settings().osdf());
            if (!currentVersion.olderThan(configsVersion)) return;
            update(configsVersion);
        } catch (Exception e) {
            warn("Can't auto-update: " + e.getClass().getSimpleName() + " " + e.getMessage() +" Try manual update or install new version");
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

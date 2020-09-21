package io.osdf.actions.system.update;

import io.osdf.settings.paths.OsdfPaths;
import io.osdf.settings.version.OsdfVersion;
import lombok.RequiredArgsConstructor;

import static io.osdf.actions.system.install.OsdfInstaller.osdfInstaller;
import static io.osdf.actions.system.install.jarinstaller.RemoteJarInstaller.jarInstaller;

@RequiredArgsConstructor
public class UpdateCommand {
    private final OsdfPaths paths;

    public static UpdateCommand updateCommand(OsdfPaths paths) {
        return new UpdateCommand(paths);
    }

    public void update(OsdfVersion version) {
        osdfInstaller(paths, jarInstaller(version, paths), false, false).install();
    }
}

package io.microconfig.osdf.api.implementations;

import io.microconfig.osdf.api.declarations.InstallApi;
import io.microconfig.osdf.commands.InstallCommand;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.install.jarinstaller.JarInstaller;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.install.jarinstaller.FakeJarInstaller.fakeJarInstaller;
import static io.microconfig.osdf.install.jarinstaller.LocalJarInstaller.jarInstaller;
import static io.osdf.settings.paths.OsdfPaths.paths;
import static io.microconfig.osdf.state.OSDFVersion.fromString;
import static io.microconfig.osdf.utils.JarUtils.isJar;

@RequiredArgsConstructor
public class InstallApiImpl implements InstallApi {
    private final OsdfPaths paths;

    public static InstallApi installApi(OsdfPaths paths) {
        return new InstallApiImpl(paths);
    }

    @Override
    public void install(Boolean noBashRc, Boolean clearState) {
        if (!isJar() && paths.root().equals(paths().root())) throw new OSDFException("Installation is possible only using jar file");
        JarInstaller jarInstaller = isJar() ? jarInstaller(paths) : fakeJarInstaller(paths, fromString("1.0.0"));
        new InstallCommand(paths, jarInstaller, clearState, noBashRc).install();
    }
}

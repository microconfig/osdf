package io.microconfig.osdf.api.implementations;

import io.microconfig.osdf.api.declarations.InstallApi;
import io.microconfig.osdf.commands.InstallCommand;
import io.microconfig.osdf.install.jarinstaller.JarInstaller;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.install.jarinstaller.FakeJarInstaller.fakeJarInstaller;
import static io.microconfig.osdf.install.jarinstaller.LocalJarInstaller.jarInstaller;
import static io.microconfig.osdf.state.OSDFVersion.fromString;
import static io.microconfig.osdf.utils.JarUtils.isJar;

@RequiredArgsConstructor
public class InstallApiImpl implements InstallApi {
    private final OSDFPaths paths;

    public static InstallApi installApi(OSDFPaths paths) {
        return new InstallApiImpl(paths);
    }

    @Override
    public void install(Boolean noBashRc, Boolean clearState) {
        JarInstaller jarInstaller = isJar() ? jarInstaller(paths) : fakeJarInstaller(paths, fromString("1.0.0"));
        new InstallCommand(paths, jarInstaller, clearState, noBashRc).install();
    }
}

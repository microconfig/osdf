package io.microconfig.osdf.commands;


import io.microconfig.osdf.install.jarinstaller.JarInstaller;
import io.osdf.settings.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.install.OSDFInstaller.osdfInstaller;
import static io.microconfig.utils.Logger.announce;

@RequiredArgsConstructor
public class InstallCommand {
    private final OSDFPaths paths;
    private final JarInstaller jarInstaller;
    private final boolean clearState;
    private final boolean noBashRc;

    public void install() {
        osdfInstaller(paths, jarInstaller, clearState, noBashRc).install();
        announce("Installed " + jarInstaller.version());
    }
}

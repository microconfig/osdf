package io.microconfig.osdf.commands;


import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.install.OSDFInstaller;
import io.microconfig.osdf.install.OSDFSource;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.state.OSDFVersion;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

import static io.microconfig.osdf.install.AutoCompleteInstaller.autoCompleteInstaller;
import static io.microconfig.osdf.install.OSDFInstaller.osdfInstaller;
import static io.microconfig.osdf.install.OSDFSource.LOCAL;
import static io.microconfig.osdf.state.OSDFState.fromFile;
import static io.microconfig.osdf.state.OSDFVersion.fromState;
import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static io.microconfig.osdf.utils.JarUtils.isJar;
import static io.microconfig.utils.Logger.announce;
import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.exists;

@RequiredArgsConstructor
public class InstallCommand {
    private final OSDFPaths paths;
    private final OSDFVersion version;
    private final boolean noBashRc;
    private final boolean clearState;

    public void install() {
        clearStateIfNecessary();
        OSDFSource osdfSource = isJar() ? LOCAL : null;
        OSDFInstaller installer = osdfInstaller(paths, noBashRc);
        if (foldersExist()) {
            installer.install(fromState(fromFile(paths.stateSavePath())), version, osdfSource, false);
        } else {
            createWorkfolder();
            installer.install(null, version, osdfSource, true);
            autoCompleteInstaller(paths.componentsPath()).installAutoComplete(false);
        }
        announce("Installed " + version);
    }

    private void clearStateIfNecessary() {
        if (clearState) {
            execute("rm -rf " + paths.root());
        }
    }

    private boolean foldersExist() {
        return exists(paths.root()) && exists(paths.scriptFolder());
    }

    private void createWorkfolder() {
        try {
            createDirectory(paths.root());
            createDirectory(paths.scriptFolder());
            createDirectory(paths.settingsRootPath());
        } catch (IOException e) {
            throw new OSDFException("Couldn't create workfolder at path " + paths.root(), e);
        }
    }
}

package io.osdf.actions.system.install;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.actions.system.install.jarinstaller.JarInstaller;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static io.osdf.actions.system.install.BashrcInstaller.bashrcInstaller;
import static io.osdf.actions.system.install.ScriptInstaller.scriptInstaller;
import static io.osdf.actions.system.install.WorkfolderInstaller.workfolderInstaller;
import static io.osdf.actions.system.install.YamlFileReplacer.yamlFileReplacer;
import static io.osdf.settings.version.OsdfVersionFile.osdfVersionFile;
import static io.osdf.common.utils.JarUtils.pathToJava;
import static io.osdf.common.utils.ProcessUtil.startAndWait;
import static io.microconfig.utils.Logger.announce;
import static java.util.List.of;

@RequiredArgsConstructor
public class OSDFInstaller {
    private final OsdfPaths paths;
    private final JarInstaller jarInstaller;
    private final boolean clearState;
    private final boolean noBashRc;
    private final boolean withMigrations;

    public static OSDFInstaller osdfInstaller(OsdfPaths paths, JarInstaller jarInstaller, boolean clearState, boolean noBashRc) {
        return new OSDFInstaller(paths, jarInstaller, clearState, noBashRc, true);
    }

    public void install() {
        boolean cleanInstallation = workfolderInstaller(paths).install(clearState);

        List<FileReplacer> newFiles = newFiles();
        newFiles.forEach(FileReplacer::prepare);
        newFiles.forEach(FileReplacer::replace);
        if (withMigrations && !cleanInstallation) {
            migrate();
        }
        announce("Successfully installed " + jarInstaller.version());
    }

    private void migrate() {
        List<String> processArgs = of(pathToJava(),  "-jar", paths.root() + "/osdf.jar", "migrate");
        int exitCode = startAndWait(new ProcessBuilder(processArgs).inheritIO());
        if (exitCode != 0) throw new OSDFException("Migration failed. Please reinstall osdf completely using -c flag");
    }

    private List<FileReplacer> newFiles() {
        List<FileReplacer> newFiles = new ArrayList<>();
        newFiles.add(jarInstaller);
        newFiles.add(versionFile());
        newFiles.add(scriptInstaller(paths));
        if (!noBashRc) {
            newFiles.add(bashrcInstaller(paths));
        }
        return newFiles;
    }

    private FileReplacer versionFile() {
        return yamlFileReplacer(osdfVersionFile(jarInstaller.version()), "version", paths, paths.settings().osdf());
    }
}

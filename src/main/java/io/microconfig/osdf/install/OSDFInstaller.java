package io.microconfig.osdf.install;

import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.install.jarinstaller.JarInstaller;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static io.microconfig.osdf.install.AutoCompleteInstaller.autoCompleteInstaller;
import static io.microconfig.osdf.install.BashrcInstaller.bashrcInstaller;
import static io.microconfig.osdf.install.ScriptInstaller.scriptInstaller;
import static io.microconfig.osdf.install.WorkfolderInstaller.workfolderInstaller;
import static io.microconfig.osdf.install.YamlFileReplacer.yamlFileReplacer;
import static io.microconfig.osdf.state.OSDFVersionFile.osdfVersionFile;
import static io.microconfig.osdf.utils.JarUtils.pathToJava;
import static io.microconfig.osdf.utils.ProcessUtil.startAndWait;
import static io.microconfig.utils.Logger.announce;
import static java.util.List.of;

@RequiredArgsConstructor
public class OSDFInstaller {
    private final OSDFPaths paths;
    private final JarInstaller jarInstaller;
    private final boolean clearState;
    private final boolean noBashRc;
    private final boolean withMigrations;

    public static OSDFInstaller osdfInstaller(OSDFPaths paths, JarInstaller jarInstaller, boolean clearState, boolean noBashRc) {
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
        newFiles.add(clearState ? autoCompleteInstaller(paths, true)
                : autoCompleteInstaller(paths, false));
        if (!noBashRc) {
            newFiles.add(bashrcInstaller(paths));
        }
        return newFiles;
    }

    private FileReplacer versionFile() {
        return yamlFileReplacer(osdfVersionFile(jarInstaller.version()), "version", paths, paths.settings().osdf());
    }
}

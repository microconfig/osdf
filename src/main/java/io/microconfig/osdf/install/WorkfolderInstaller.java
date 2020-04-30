package io.microconfig.osdf.install;

import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.utils.FileUtils;
import lombok.RequiredArgsConstructor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static java.util.List.of;

@RequiredArgsConstructor
public class WorkfolderInstaller {
    private final OSDFPaths paths;
    private final List<Path> dirs;

    public static WorkfolderInstaller workfolderInstaller(OSDFPaths paths) {
        return new WorkfolderInstaller(paths, of(paths.root(), paths.bin(), paths.settingsRoot(), paths.tmp()));
    }

    public boolean install(boolean clear) {
        if (clear) execute("rm -rf " + paths.root());
        if (foldersExist()) {
            return false;
        }
        createFolders();
        return true;
    }

    private boolean foldersExist() {
        return dirs.stream().allMatch(Files::exists);
    }

    private void createFolders() {
        dirs.forEach(FileUtils::createDirectoryIfNotExists);
    }
}

package io.osdf.actions.system.install;

import io.osdf.common.utils.FileUtils;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static io.osdf.common.utils.FileUtils.delete;
import static java.util.List.of;

@RequiredArgsConstructor
public class WorkfolderInstaller {
    private final OsdfPaths paths;
    private final List<Path> dirs;

    public static WorkfolderInstaller workfolderInstaller(OsdfPaths paths) {
        return new WorkfolderInstaller(paths, of(paths.root(), paths.bin(), paths.settingsRoot(), paths.tmp()));
    }

    public boolean install(boolean clear) {
        if (clear) delete(paths.root());
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

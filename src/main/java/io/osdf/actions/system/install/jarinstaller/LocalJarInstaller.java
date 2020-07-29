package io.osdf.actions.system.install.jarinstaller;

import io.osdf.settings.paths.OsdfPaths;
import io.osdf.settings.version.OsdfVersion;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.osdf.common.utils.FileUtils.move;
import static io.osdf.common.utils.JarUtils.jarPath;
import static io.osdf.settings.version.OsdfVersion.fromJarPath;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class LocalJarInstaller implements JarInstaller {
    private final Path jarPath;
    private final OsdfPaths paths;

    public static LocalJarInstaller jarInstaller(OsdfPaths paths) {
        return new LocalJarInstaller(jarPath(), paths);
    }

    @Override
    public OsdfVersion version() {
        return fromJarPath(jarPath);
    }

    @Override
    public void prepare() {
        move(jarPath, of(paths.tmp() + "/osdf.jar"));
    }

    @Override
    public void replace() {
        move(of(paths.tmp() + "/osdf.jar"), of(paths.root() + "/osdf.jar"));
    }
}

package io.osdf.actions.system.install.jarinstaller;

import io.osdf.settings.paths.OsdfPaths;
import io.osdf.settings.version.OsdfVersion;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.osdf.common.utils.FileUtils.copy;
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
        copy(jarPath, of(paths.tmp() + "/" + JAR_NAME));
    }

    @Override
    public void replace() {
        move(of(paths.tmp() + "/" + JAR_NAME), of(paths.root() + "/" + JAR_NAME));
    }
}

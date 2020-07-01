package io.osdf.actions.system.install.jarinstaller;

import io.osdf.settings.paths.OsdfPaths;
import io.osdf.settings.version.OsdfVersion;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.osdf.settings.version.OsdfVersion.fromJarPath;
import static io.osdf.common.utils.CommandLineExecutor.execute;
import static io.osdf.common.utils.JarUtils.jarPath;

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
        execute("cp " + jarPath + " " + paths.tmp() + "/osdf.jar");
    }

    @Override
    public void replace() {
        execute("mv " + paths.tmp() + "/osdf.jar " + paths.root() + "/osdf.jar");
    }
}

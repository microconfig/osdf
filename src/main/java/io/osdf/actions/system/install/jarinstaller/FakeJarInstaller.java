package io.osdf.actions.system.install.jarinstaller;

import io.osdf.settings.paths.OsdfPaths;
import io.osdf.settings.version.OsdfVersion;
import lombok.RequiredArgsConstructor;

import static io.osdf.common.utils.FileUtils.move;
import static io.osdf.common.utils.FileUtils.writeStringToFile;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class FakeJarInstaller implements JarInstaller {
    private final OsdfPaths paths;
    private final OsdfVersion version;

    public static FakeJarInstaller fakeJarInstaller(OsdfPaths paths, OsdfVersion version) {
        return new FakeJarInstaller(paths, version);
    }

    @Override
    public OsdfVersion version() {
        return version;
    }

    @Override
    public void prepare() {
        writeStringToFile(of(paths.tmp() + "/osdf.jar"), "fake");
    }

    @Override
    public void replace() {
        move(of(paths.tmp() + "/osdf.jar"), of(paths.root() + "/osdf.jar"));
    }
}

package io.microconfig.osdf.install.jarinstaller;

import io.osdf.settings.paths.OsdfPaths;
import io.microconfig.osdf.state.OSDFVersion;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static io.microconfig.osdf.utils.FileUtils.writeStringToFile;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class FakeJarInstaller implements JarInstaller {
    private final OsdfPaths paths;
    private final OSDFVersion version;

    public static FakeJarInstaller fakeJarInstaller(OsdfPaths paths, OSDFVersion version) {
        return new FakeJarInstaller(paths, version);
    }

    @Override
    public OSDFVersion version() {
        return version;
    }

    @Override
    public void prepare() {
        writeStringToFile(of(paths.tmp() + "/osdf.jar"), "fake");
    }

    @Override
    public void replace() {
        execute("cp " + paths.tmp() + "/osdf.jar " + paths.root() + "/osdf.jar");
    }
}

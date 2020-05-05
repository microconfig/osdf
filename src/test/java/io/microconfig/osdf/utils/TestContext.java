package io.microconfig.osdf.utils;

import io.microconfig.osdf.paths.OSDFPaths;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.install.OSDFInstaller.osdfInstaller;
import static io.microconfig.osdf.install.jarinstaller.FakeJarInstaller.fakeJarInstaller;
import static io.microconfig.osdf.state.OSDFVersion.fromString;
import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static io.microconfig.osdf.utils.DefaultConfigsCreator.defaultConfigsCreator;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class TestContext {
    @Getter
    private final OSDFPaths paths;

    public static TestContext defaultContext() {
        return new TestContext(new OSDFPaths(of("/tmp/osdf/.osdf")));
    }

    public void install() {
        osdfInstaller(paths, fakeJarInstaller(paths, fromString("1.0.0")), true, true).install();
    }

    public void createDefaultConfigs() {
        defaultConfigsCreator(paths).create();
    }

    public void clear() {
        execute("rm -rf " + paths.root());
    }
}

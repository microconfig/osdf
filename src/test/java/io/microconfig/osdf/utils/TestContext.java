package io.microconfig.osdf.utils;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;

import static io.microconfig.osdf.api.implementations.InitializationApiImpl.initializationApi;
import static io.microconfig.osdf.common.Credentials.of;
import static io.microconfig.osdf.install.OSDFInstaller.osdfInstaller;
import static io.microconfig.osdf.install.jarinstaller.FakeJarInstaller.fakeJarInstaller;
import static io.microconfig.osdf.state.OSDFVersion.fromString;
import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static io.microconfig.osdf.utils.ConfigUnzipper.configUnzipper;
import static io.microconfig.osdf.utils.DefaultConfigsCreator.defaultConfigsCreator;
import static java.nio.file.Files.exists;
import static org.mockito.Mockito.mock;

@RequiredArgsConstructor
public class TestContext {
    public static final Path OSDF_PATH = Path.of("/tmp/osdf/.osdf");
    public static final Path CONFIGS_PATH = Path.of("/tmp/osdf/configs");

    @Getter
    private final OSDFPaths paths;

    public static TestContext defaultContext() {
        return new TestContext(new OSDFPaths(OSDF_PATH));
    }

    public void install() {
        if (!exists(Path.of("/tmp/osdf"))) {
            execute("mkdir /tmp/osdf");
        }
        osdfInstaller(paths, fakeJarInstaller(paths, fromString("1.0.0")), true, true).install();
    }

    public void createDefaultConfigs() {
        defaultConfigsCreator(paths).create();
    }

    public void clear() {
        execute("rm -rf " + paths.root());
    }

    public void prepareConfigs() throws IOException {
        configUnzipper(CONFIGS_PATH, "configs.zip").unzip();
    }

    public void initDev() throws IOException {
        install();
        prepareConfigs();
        initializationApi(paths, mock(ClusterCLI.class)).openshift(of("user:pass"), null, false);
        initializationApi(paths, mock(ClusterCLI.class)).localConfigs(CONFIGS_PATH, null);
        initializationApi(paths, mock(ClusterCLI.class)).configs("dev", null);
    }
}

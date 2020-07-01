package io.osdf.common.utils;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;

import static io.osdf.actions.init.api.InitializationApiImpl.initializationApi;
import static io.osdf.common.Credentials.of;
import static io.osdf.actions.system.install.OSDFInstaller.osdfInstaller;
import static io.osdf.actions.system.install.jarinstaller.FakeJarInstaller.fakeJarInstaller;
import static io.osdf.settings.version.OsdfVersion.fromString;
import static io.osdf.common.utils.CommandLineExecutor.execute;
import static io.osdf.common.utils.ConfigUnzipper.configUnzipper;
import static io.osdf.common.utils.DefaultConfigsCreator.defaultConfigsCreator;
import static java.nio.file.Files.exists;
import static org.mockito.Mockito.mock;

@RequiredArgsConstructor
public class TestContext {
    public static final Path OSDF_PATH = Path.of("/tmp/osdf/.osdf");
    public static final Path CONFIGS_PATH = Path.of("/tmp/osdf/configs");

    @Getter
    private final OsdfPaths paths;

    public static TestContext defaultContext() {
        return new TestContext(new OsdfPaths(OSDF_PATH));
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
        initializationApi(paths, mock(ClusterCli.class)).openshift(of("user:pass"), null, false);
        initializationApi(paths, mock(ClusterCli.class)).localConfigs(CONFIGS_PATH, null);
        initializationApi(paths, mock(ClusterCli.class)).configs("dev", null);
    }
}

package io.osdf.common.utils;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.osdf.actions.init.InitializationApiImpl.initializationApi;
import static io.osdf.actions.system.install.OsdfInstaller.osdfInstaller;
import static io.osdf.actions.system.install.jarinstaller.FakeJarInstaller.fakeJarInstaller;
import static io.osdf.common.Credentials.of;
import static io.osdf.common.utils.CommandLineExecutor.execute;
import static io.osdf.common.utils.DefaultConfigsCreator.defaultConfigsCreator;
import static io.osdf.settings.version.OsdfVersion.fromString;
import static java.nio.file.Files.exists;
import static java.util.Objects.requireNonNull;
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

    public void prepareConfigs() {
        if (exists(CONFIGS_PATH)) {
            execute("rm -rf " + CONFIGS_PATH);
        }
        String dir = requireNonNull(ConfigUnzipper.class.getClassLoader().getResource("configs")).getPath();
        execute("cp -r " + dir + " " + CONFIGS_PATH);
    }

    public void initDev() {
        install();
        prepareConfigs();
        initializationApi(paths, mock(ClusterCli.class)).openshift(of("user:pass"), null, false);
        initializationApi(paths, mock(ClusterCli.class)).localConfigs(CONFIGS_PATH, null);
        initializationApi(paths, mock(ClusterCli.class)).configs("dev", null);
    }
}

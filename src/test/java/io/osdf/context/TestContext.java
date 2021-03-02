package io.osdf.context;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Path;

import static io.osdf.actions.init.InitializationApiImpl.initializationApi;
import static io.osdf.actions.system.install.OsdfInstaller.osdfInstaller;
import static io.osdf.actions.system.install.jarinstaller.FakeJarInstaller.fakeJarInstaller;
import static io.osdf.common.Credentials.of;
import static io.osdf.context.DefaultConfigsCreator.defaultConfigsCreator;
import static io.osdf.settings.version.OsdfVersion.fromString;
import static java.nio.file.Files.*;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.mockito.Mockito.mock;

@RequiredArgsConstructor
public class TestContext {
    @Getter
    private final OsdfPaths paths;
    private final Path tmpDir;

    @SneakyThrows
    public static TestContext defaultContext() {
        Path tmpDir = createTempDirectory("osdf");
        return new TestContext(new OsdfPaths(Path.of(tmpDir + "/.osdf")), tmpDir);
    }

    public void install() {
        osdfInstaller(paths, fakeJarInstaller(paths, fromString("1.0.0")), true, true).install();
    }

    public void createDefaultConfigs() {
        defaultConfigsCreator(paths).create();
    }

    @SneakyThrows
    public void clear() {
        deleteDirectory(configsPath().toFile());
        deleteDirectory(paths.root().toFile());
    }

    @SneakyThrows
    public void prepareConfigs() {
        String dir = requireNonNull(TestContext.class.getClassLoader().getResource("configs")).getPath();
        copyDirectory(new File(dir), configsPath().toFile());
    }

    public void initDev() {
        install();
        prepareConfigs();
        initializationApi(paths, mock(ClusterCli.class)).openshift(of("user:pass"), null, false);
        initializationApi(paths, mock(ClusterCli.class)).localConfigs(configsPath(), null);
        initializationApi(paths, mock(ClusterCli.class)).configs("dev", null, null);
    }

    @SneakyThrows
    public Path configsPath() {
        Path configsPath = Path.of(tmpDir + "/configs");

        if (!exists(configsPath)) createDirectory(configsPath);
        return configsPath;
    }
}

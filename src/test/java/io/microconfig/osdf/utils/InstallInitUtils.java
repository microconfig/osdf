package io.microconfig.osdf.utils;

import io.microconfig.osdf.commands.InitCommand;
import io.microconfig.osdf.commands.InstallCommand;
import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.state.Credentials;
import io.microconfig.osdf.state.OSDFVersion;

import java.io.IOException;
import java.nio.file.Path;

import static io.microconfig.osdf.utils.CommandLineExecutor.execute;

public class InstallInitUtils {
    public static final Path DEFAULT_CONFIGS_PATH = Path.of("/tmp/configs");
    public static final Path DEFAULT_OSDF_PATH = Path.of("/tmp/osdf");

    public static void installInit(Path configsPath, Path osdfPath, OSDFPaths paths) {
        execute("rm -rf " + osdfPath);
        new InstallCommand(paths, OSDFVersion.fromString("1.0.0"), true).install();
        new InitCommand(paths).run(null, null, null, configsPath,
                null, Credentials.of("test:test"), null, "dev",
                null, "helloworld", null, null);
    }

    public static OSDFPaths createConfigsAndInstallInit() throws IOException {
        ConfigUnzipper.unzip("configs.zip", DEFAULT_CONFIGS_PATH);
        OSDFPaths paths = new OSDFPaths(DEFAULT_OSDF_PATH);
        installInit(DEFAULT_CONFIGS_PATH, DEFAULT_OSDF_PATH, paths);
        return paths;
    }
}

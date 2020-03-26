package io.microconfig.osdf.utils;

import io.microconfig.osdf.commands.InitCommand;
import io.microconfig.osdf.commands.InstallCommand;
import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.state.Credentials;
import io.microconfig.osdf.state.OSDFVersion;

import java.nio.file.Path;

import static io.microconfig.osdf.utils.CommandLineExecutor.execute;

public class InstallInitUtils {
    public static void defaultInstallInit(Path configsPath, Path osdfPath, OSDFPaths paths) {
        execute("rm -rf " + osdfPath);
        new InstallCommand(paths, OSDFVersion.fromString("1.0.0")).install();
        new InitCommand(paths).run(null, null, null, configsPath,
                null, Credentials.of("test:test"), null, "dev",
                null, null, null, null);
    }
}

package io.microconfig.osdf.commands;

import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.state.OSDFVersion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static java.nio.file.Path.of;

class InstallCommandTest {
    private OSDFPaths paths;
    private final Path osdfPath = of("/tmp/osdf");

    @BeforeEach
    void createConfigs() {
        execute("rm -rf " + osdfPath);
        paths = new OSDFPaths(osdfPath);
    }

    @Test
    void firstInstall() {
        new InstallCommand(paths, OSDFVersion.fromString("1.0.0")).install();
    }

    @Test
    void secondInstall() {
        new InstallCommand(paths, OSDFVersion.fromString("1.0.0")).install();
        new InstallCommand(paths, OSDFVersion.fromString("1.1.0")).install();
    }
}
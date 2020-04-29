package io.microconfig.osdf.commands;

import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.state.OSDFVersion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static io.microconfig.osdf.utils.InstallInitUtils.DEFAULT_OSDF_PATH;

class InstallCommandTest {
    private OSDFPaths paths;

    @BeforeEach
    void createConfigs() {
        execute("rm -rf " + DEFAULT_OSDF_PATH);
        paths = new OSDFPaths(DEFAULT_OSDF_PATH);
    }

    @Test
    void firstInstall() {
        new InstallCommand(paths, OSDFVersion.fromString("1.0.0"), true, false).install();
    }

    @Test
    void secondInstall() {
        new InstallCommand(paths, OSDFVersion.fromString("1.0.0"), true, false).install();
        new InstallCommand(paths, OSDFVersion.fromString("1.1.0"), true, false).install();
    }
}
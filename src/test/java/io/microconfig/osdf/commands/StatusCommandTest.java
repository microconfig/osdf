package io.microconfig.osdf.commands;

import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.utils.ConfigUnzipper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static io.microconfig.osdf.utils.InstallInitUtils.defaultInstallInit;
import static java.nio.file.Path.of;

class StatusCommandTest {
    private OSDFPaths paths;
    private Path configsPath = of("/tmp/configs");
    private Path osdfPath = of("/tmp/osdf");

    @BeforeEach
    void createConfigs() throws IOException {
        ConfigUnzipper.unzip("configs.zip", configsPath);
        paths = new OSDFPaths(osdfPath);
    }

    @Test
    void statusOk() {
        defaultInstallInit(configsPath, osdfPath, paths);
    }
}
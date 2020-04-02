package io.microconfig.osdf.commands;

import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.state.Credentials;
import io.microconfig.osdf.state.OSDFState;
import io.microconfig.osdf.utils.ConfigUnzipper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static io.microconfig.osdf.state.ConfigSource.LOCAL;
import static io.microconfig.osdf.utils.InstallInitUtils.defaultInstallInit;
import static java.nio.file.Path.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InitCommandTest {
    private OSDFPaths paths;
    private final Path configsPath = of("/tmp/configs");
    private final Path osdfPath = of("/tmp/osdf");

    @BeforeEach
    void createConfigs() throws IOException {
        ConfigUnzipper.unzip("configs.zip", configsPath);
        paths = new OSDFPaths(osdfPath);
    }

    @Test
    void initLocalCommand() {
        defaultInstallInit(configsPath, osdfPath, paths);
        OSDFState osdfState = OSDFState.fromFile(paths.stateSavePath());
        assertEquals(LOCAL, osdfState.getConfigSource());
        assertEquals("dev", osdfState.getEnv());
        assertEquals(Credentials.of("test:test"), osdfState.getOpenShiftCredentials());
        assertNull(osdfState.getGitUrl());
        assertNull(osdfState.getNexusUrl());
        assertNull(osdfState.getConfigsNexusArtifact());
        assertNull(osdfState.getNexusCredentials());
        assertNull(osdfState.getConfigVersion());
        assertEquals("helloworld", osdfState.getGroup());
        assertNull(osdfState.getProjectVersion());
        assertNull(osdfState.getComponents());
    }

}
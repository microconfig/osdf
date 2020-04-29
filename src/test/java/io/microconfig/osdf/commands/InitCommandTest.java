package io.microconfig.osdf.commands;

import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.state.OSDFState;
import io.microconfig.osdf.openshift.OpenShiftCredentials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.microconfig.osdf.configs.ConfigsSource.LOCAL;
import static io.microconfig.osdf.utils.InstallInitUtils.createConfigsAndInstallInit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InitCommandTest {
    private OSDFPaths paths;

    @BeforeEach
    void createConfigs() throws IOException {
        paths = createConfigsAndInstallInit();
    }

    @Test
    void initLocalCommand() {
        OSDFState osdfState = OSDFState.fromFile(paths.stateSavePath());
        assertEquals(LOCAL, osdfState.getConfigsSource());
        assertEquals("dev", osdfState.getEnv());
        assertEquals(OpenShiftCredentials.of("test:test"), osdfState.getOpenShiftCredentials());
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
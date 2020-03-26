package io.microconfig.osdf.state;

import org.junit.jupiter.api.Test;

import static io.microconfig.osdf.nexus.NexusArtifact.configsNexusArtifact;
import static io.microconfig.osdf.state.ConfigSource.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OSDFStateTest {
    @Test
    void checkEmptyState() {
        OSDFState state = new OSDFState();
        assertFalse(state.check());
    }

    @Test
    void checkBadStateWithoutConfigSource() {
        OSDFState state = new OSDFState();
        state.setEnv("dev");
        state.setOpenShiftCredentials(Credentials.of("test:test"));
        assertFalse(state.check());
    }

    @Test
    void checkBadStateWithoutCredentials() {
        OSDFState state = new OSDFState();
        state.setEnv("dev");
        state.setConfigSource(LOCAL);
        state.setLocalConfigs("/some/path");
        assertFalse(state.check());
    }

    @Test
    void checkBadStateWithoutEnv() {
        OSDFState state = new OSDFState();
        state.setOpenShiftCredentials(Credentials.of("test:test"));
        state.setConfigSource(LOCAL);
        state.setLocalConfigs("/some/path");
        assertFalse(state.check());
    }

    @Test
    void checkStateWithLocalConfigs() {
        OSDFState state = new OSDFState();
        state.setOpenShiftCredentials(Credentials.of("test:test"));
        state.setConfigSource(LOCAL);
        state.setLocalConfigs("/some/path");
        state.setEnv("dev");
        assertTrue(state.check());
    }

    @Test
    void checkStateWithGitConfigs() {
        OSDFState state = new OSDFState();
        state.setOpenShiftCredentials(Credentials.of("test:test"));
        state.setEnv("dev");
        state.setConfigSource(GIT);
        state.setGitUrl("git.url");
        state.setConfigVersion("master");
        assertTrue(state.check());
    }

    @Test
    void checkStateWithNexusConfigs() {
        OSDFState state = new OSDFState();
        state.setOpenShiftCredentials(Credentials.of("test:test"));
        state.setEnv("dev");
        state.setConfigSource(NEXUS);
        state.setNexusUrl("nexus.url");
        state.setConfigVersion("1.1.1");
        state.setConfigsNexusArtifact(configsNexusArtifact("group:artifact"));
        assertTrue(state.check());
    }
}
package io.microconfig.osdf.state;

import org.junit.jupiter.api.Test;

import static io.microconfig.osdf.nexus.NexusArtifact.configsNexusArtifact;
import static io.microconfig.osdf.state.ConfigSource.*;
import static io.microconfig.osdf.state.OSDFStatePrinter.statePrinter;

class OSDFStatePrinterTest {
    @Test
    void printEmptyState() {
        OSDFState state = new OSDFState();
        state.setOsdfVersion("1.1.1");
        statePrinter(state).print();
    }

    @Test
    void printInitializedStateWithLocalConfigs() {
        OSDFState state = new OSDFState();
        state.setOsdfVersion("1.1.1");
        state.setOpenShiftCredentials(Credentials.of("test:test"));
        state.setConfigSource(LOCAL);
        state.setLocalConfigs("/some/path");
        state.setEnv("dev");
        statePrinter(state).print();
    }

    @Test
    void printInitializedStateWithGitConfigs() {
        OSDFState state = new OSDFState();
        state.setOsdfVersion("1.1.1");
        state.setOpenShiftCredentials(Credentials.of("test:test"));
        state.setEnv("dev");
        state.setConfigSource(GIT);
        state.setGitUrl("git.url");
        state.setConfigVersion("master");
        statePrinter(state).print();
    }

    @Test
    void printInitializedStateWithNexusConfigs() {
        OSDFState state = new OSDFState();
        state.setOsdfVersion("1.1.1");
        state.setOpenShiftCredentials(Credentials.of("test:test"));
        state.setEnv("dev");
        state.setConfigSource(NEXUS);
        state.setNexusUrl("nexus.url");
        state.setConfigVersion("1.1.1");
        state.setConfigsNexusArtifact(configsNexusArtifact("group:artifact"));
        statePrinter(state).print();
    }
}
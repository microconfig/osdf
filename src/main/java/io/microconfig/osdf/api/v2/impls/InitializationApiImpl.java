package io.microconfig.osdf.api.v2.impls;

import io.microconfig.osdf.api.v2.apis.InitializationApi;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.nexus.NexusArtifact;
import io.microconfig.osdf.state.Credentials;
import io.microconfig.osdf.state.OpenShiftCredentials;

import java.nio.file.Path;

public class InitializationApiImpl implements InitializationApi {
    public static InitializationApi initializationApi() {
        return new InitializationApiImpl();
    }

    @Override
    public void gitConfigs(String gitUrl, String configVersion) {
        throw new OSDFException("Not Implemented yet");
    }

    @Override
    public void nexusConfigs(String nexusUrl, NexusArtifact configsNexusArtifact, String configVersion, Credentials nexusCredentials) {
        throw new OSDFException("Not Implemented yet");
    }

    @Override
    public void localConfigs(Path localConfigs) {
        throw new OSDFException("Not Implemented yet");
    }

    @Override
    public void openshift(OpenShiftCredentials openShiftCredentials) {
        throw new OSDFException("Not Implemented yet");
    }

    @Override
    public void config(String env, String projVersion) {
        throw new OSDFException("Not Implemented yet");
    }

    @Override
    public void pull() {
        throw new OSDFException("Not Implemented yet");
    }
}

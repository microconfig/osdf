package io.microconfig.osdf.state;

import io.microconfig.osdf.api.parameter.*;
import io.microconfig.osdf.parameters.CommandLineParameter;
import lombok.RequiredArgsConstructor;

import static io.microconfig.utils.Logger.error;

@RequiredArgsConstructor
public class OSDFStateChecker {
    private final OSDFState state;

    public static OSDFStateChecker stateChecker(OSDFState state) {
        return new OSDFStateChecker(state);
    }

    public boolean checkConfigSource() {
        if (!notNull(state.getConfigSource(), new ConfigSourceParameter())) return false;
        switch (state.getConfigSource()) {
            case LOCAL: return areLocalConfigsConfigured();
            case GIT: return isGitConfigured();
            case NEXUS: return isNexusConfigured();
        }
        return false;
    }

    public boolean checkOpenShiftCredentials() {
        return notNull(state.getOpenShiftCredentials(), new OpenShiftCredentialsParameter());
    }

    public boolean checkEnv() {
        return notNull(state.getEnv(), new EnvParameter());
    }

    private boolean isGitConfigured() {
        return checkConfigVersion() && notNull(state.getGitUrl(), new GitUrlParameter());
    }

    private boolean isNexusConfigured() {
        return checkConfigVersion()
                && notNull(state.getNexusUrl(), new NexusUrlParameter())
                && notNull(state.getConfigsNexusArtifact(), new ConfigsNexusArtifactParameter());
    }

    private boolean areLocalConfigsConfigured() {
        return notNull(state.getLocalConfigs(), new LocalConfigsParameter());
    }

    private boolean checkConfigVersion() {
        return notNull(state.getConfigVersion(), new ConfigVersionParameter());
    }

    private boolean notNull(Object object, CommandLineParameter<?> parameter) {
        if (object == null) {
            error(parameter.missingHint());
            return false;
        }
        return true;
    }
}

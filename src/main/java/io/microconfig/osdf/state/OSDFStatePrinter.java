package io.microconfig.osdf.state;


import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.configfetcher.GitFetcher.urlWithoutPassword;
import static io.microconfig.utils.Logger.announce;

@RequiredArgsConstructor
public class OSDFStatePrinter {
    private final OSDFState state;

    public static OSDFStatePrinter statePrinter(OSDFState state) {
        return new OSDFStatePrinter(state);
    }

    public void print() {
        String version = "OSDF version: " + state.getOsdfVersion() + "\n";

        String stateParameters = "OSDF is not initialized\n";
        if (state.check()) stateParameters = configSourceString() + "\n" +
                "OpenShift user: " + state.getOpenShiftCredentials().getUsername() + "\n" +
                "Env: " + state.getEnv() + "\n" +
                "Project version: " + (state.getProjectVersion() == null ? "<default>" : state.getProjectVersion()) + "\n" +
                "Group: " + (state.getGroup() == null ? "<all>" : state.getGroup()) + "\n" +
                "Components: " + (state.getComponents() != null && !state.getComponents().isEmpty() ? state.getComponents().toString() : "<all>");
        announce(version + stateParameters);
    }

    private String configSourceString() {
        String source = "";
        String version = null;
        switch (state.getConfigSource()) {
            case GIT:
                source = urlWithoutPassword(state.getGitUrl());
                version = state.getConfigVersion();
                break;
            case NEXUS:
                source = state.getNexusUrl();
                version = state.getConfigVersion();
                break;
            case LOCAL:
                source = state.getLocalConfigs();
                break;
        }
        String result = "Config source: " + source;
        if (version != null) result += "\nConfig version: " + version;
        return result;
    }
}

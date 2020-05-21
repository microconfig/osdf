package io.microconfig.osdf.openshift;

import io.microconfig.osdf.microconfig.properties.OpenShiftProperties;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.microconfig.properties.OpenShiftProperties.properties;
import static io.microconfig.osdf.microconfig.properties.PropertyGetter.propertyGetter;
import static io.microconfig.osdf.settings.SettingsFile.settingsFile;

@RequiredArgsConstructor
public class OpenShiftAuthentication {
    private final String clusterUrl;
    private final String project;

    private final OpenShiftCredentials credentials;
    private final OpenShiftCLI oc;

    public static OpenShiftAuthentication openShiftAuthentication(OSDFPaths paths, OpenShiftCLI oc) {
        OpenShiftCredentials credentials = settingsFile(OpenShiftCredentials.class, paths.settings().openshift()).getSettings();
        OpenShiftProperties properties = properties(propertyGetter(paths));
        return new OpenShiftAuthentication(properties.clusterUrl(), properties.project(), credentials, oc);
    }

    public void connect() {
        if (!isLoggedIn()) {
            login();
            setProjectCommand();
        }
    }

    private void login() {
        oc.execute("oc login " + clusterUrl + credentials.getLoginParams())
                .throwExceptionIfError();
    }

    private void setProjectCommand() {
        oc.execute("oc project " + project)
                .throwExceptionIfError();
    }

    private boolean isLoggedIn() {
        String user = oc.execute("oc whoami").getOutput().toLowerCase();
        if (user.contains("error")) return false;
        if (credentials.getCredentials() != null) {
            return user.strip().equals(credentials.getCredentials().getUsername());
        }
        return false;
    }
}
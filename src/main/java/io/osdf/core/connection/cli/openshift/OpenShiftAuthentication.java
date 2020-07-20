package io.osdf.core.connection.cli.openshift;

import io.osdf.core.connection.context.ClusterProperties;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import static io.osdf.common.SettingsFile.settingsFile;
import static io.osdf.core.connection.context.ClusterProperties.properties;

@RequiredArgsConstructor
public class OpenShiftAuthentication {
    private final String clusterUrl;
    private final String project;

    private final OpenShiftCredentials credentials;
    private final OpenShiftCli oc;

    public static OpenShiftAuthentication openShiftAuthentication(OsdfPaths paths, OpenShiftCli oc) {
        OpenShiftCredentials credentials = settingsFile(OpenShiftCredentials.class, paths.settings().openshift()).getSettings();
        ClusterProperties properties = properties(paths);
        return new OpenShiftAuthentication(properties.clusterUrl(), properties.project(), credentials, oc);
    }

    public void connect() {
        if (!isLoggedIn()) {
            login();
            setProjectCommand();
        }
    }

    private void login() {
        oc.execute("oc login " + clusterUrl + credentials.getLoginParams() + " --insecure-skip-tls-verify")
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
            return user.strip().equalsIgnoreCase(credentials.getCredentials().getUsername());
        }
        return false;
    }
}
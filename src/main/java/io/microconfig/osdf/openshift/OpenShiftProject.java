package io.microconfig.osdf.openshift;

import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.microconfig.properties.OpenShiftProperties;
import io.microconfig.osdf.state.OSDFState;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.microconfig.properties.OpenShiftProperties.properties;
import static io.microconfig.osdf.microconfig.properties.PropertyGetter.propertyGetter;
import static io.microconfig.osdf.state.OSDFState.fromFile;

@RequiredArgsConstructor
public class OpenShiftProject implements AutoCloseable {
    private final String clusterUrl;
    private final String username;
    private final String password;
    private final String project;

    private final OCExecutor oc;

    public static OpenShiftProject create(OSDFPaths paths, OCExecutor oc) {
        OSDFState state = fromFile(paths.stateSavePath());
        OpenShiftProperties properties = properties(propertyGetter(state.getEnv(), paths.configPath()));
        return new OpenShiftProject(properties.clusterUrl(), state.getOpenShiftCredentials().getUsername(),
                state.getOpenShiftCredentials().getPassword(), properties.project(), oc);
    }

    public OpenShiftProject connect() {
        if (!isLoggedIn()) {
            login();
            setProjectCommand();
        }
        return this;
    }

    @Override
    public void close() {
    }

    private void login() {
        oc.execute("oc login " + clusterUrl + " -u " + username + " -p " + password);
    }

    private void setProjectCommand() {
        oc.execute("oc project " + project);
    }

    private boolean isLoggedIn() {
        String projectString = oc.execute("oc project " + project, true).toLowerCase();
        return !projectString.contains("not a member") && !projectString.contains("please login");
    }

    @Override
    public String toString() {
        return "OpenShiftProject{" +
                "clusterUrl='" + clusterUrl + '\'' +
                ", username='" + username + '\'' +
                ", project='" + project + '\'' +
                '}';
    }
}
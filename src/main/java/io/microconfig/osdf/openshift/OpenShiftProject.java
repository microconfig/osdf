package io.microconfig.osdf.openshift;

import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.microconfig.properties.OpenShiftProperties;
import io.microconfig.osdf.state.OSDFState;
import io.microconfig.osdf.state.OpenShiftCredentials;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.microconfig.properties.OpenShiftProperties.properties;
import static io.microconfig.osdf.microconfig.properties.PropertyGetter.propertyGetter;
import static io.microconfig.osdf.state.OSDFState.fromFile;
import static java.util.List.of;

@RequiredArgsConstructor
public class OpenShiftProject implements AutoCloseable {
    private final String clusterUrl;
    private final String project;

    private final OpenShiftCredentials osCredentials;
    private final OCExecutor oc;

    public static OpenShiftProject create(OSDFPaths paths, OCExecutor oc) {
        OSDFState state = fromFile(paths.stateSavePath());
        OpenShiftProperties properties = properties(propertyGetter(state.getEnv(), paths.configPath()));
        return new OpenShiftProject(properties.clusterUrl(), properties.project(), state.getOpenShiftCredentials(), oc);
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
        oc.execute("oc login " + clusterUrl + osCredentials.getLoginParams());
    }

    private void setProjectCommand() {
        oc.execute("oc project " + project);
    }

    private boolean isLoggedIn() {
        String projectString = oc.execute("oc project " + project, true).toLowerCase();
        return of("not a member", "please login", "unauthorized")
                .stream()
                .noneMatch(projectString::contains);
    }
}
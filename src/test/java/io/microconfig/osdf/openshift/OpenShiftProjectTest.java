package io.microconfig.osdf.openshift;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

class OpenShiftProjectTest {
    private OCExecutor oc;
    private OpenShiftProject project;
    private final Map<String, String> commands = new HashMap<>();
    private OpenShiftProject projectWithToken;

    @BeforeEach
    void setUp() {
        oc = mock(OCExecutor.class);
//        when(oc.execute("oc project project", true)).thenReturn("not a member").thenReturn("ok");

        project = new OpenShiftProject("url", "project",
                OpenShiftCredentials.of("username:password"), oc);

        projectWithToken = new OpenShiftProject("url", "project",
                OpenShiftCredentials.of("oc-token"), oc);

        commands.put("login", "oc login url -u \"username\" -p \"password\"");
        commands.put("project", "oc project project");
        commands.put("loginWithToken", "oc login url --token=oc-token");
        commands.put("logout", "oc logout");
    }

    @Test
    void testConnect() {
        project.connect();
        verify(oc).execute(commands.get("project"));
        verify(oc).execute(commands.get("login"));
        verify(oc).execute(commands.get("project"));
    }

    @Test
    void testConnectWithToken() {
        projectWithToken.connect();
        verify(oc).execute(commands.get("project"));
        verify(oc).execute(commands.get("loginWithToken"));
        verify(oc).execute(commands.get("project"));
    }
}
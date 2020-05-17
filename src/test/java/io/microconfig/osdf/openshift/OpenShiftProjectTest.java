package io.microconfig.osdf.openshift;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.microconfig.osdf.commandline.CommandLineOutput.output;
import static io.microconfig.osdf.openshift.OpenShiftCredentials.of;
import static org.mockito.Mockito.*;

class OpenShiftProjectTest {
    private OCExecutor oc;
    private final Map<String, String> commands = new HashMap<>();
    private OpenShiftProject project;
    private OpenShiftProject projectWithToken;

    @BeforeEach
    void setUp() {
        commands.put("login", "oc login url -u \"username\" -p \"password\"");
        commands.put("project", "oc project default");
        commands.put("whoami", "oc whoami");
        commands.put("loginWithToken", "oc login url --token=oc-token");
        commands.put("logout", "oc logout");

        oc = mock(OCExecutor.class);
        when(oc.execute(commands.get("whoami"))).thenReturn(output("not logged in"));
        when(oc.execute(commands.get("project"))).thenReturn(output("ok"));
        when(oc.execute(commands.get("login"))).thenReturn(output("ok"));
        when(oc.execute(commands.get("loginWithToken"))).thenReturn(output("ok"));

        project = new OpenShiftProject("url", "default", of("username:password"), oc);
        projectWithToken = new OpenShiftProject("url", "default", of("oc-token"), oc);
    }

    @Test
    void testConnect() {
        project.connect();
        verify(oc).execute(commands.get("whoami"));
        verify(oc).execute(commands.get("login"));
        verify(oc).execute(commands.get("project"));
    }

    @Test
    void testConnectWithToken() {
        projectWithToken.connect();
        verify(oc).execute(commands.get("whoami"));
        verify(oc).execute(commands.get("loginWithToken"));
        verify(oc).execute(commands.get("project"));
    }
}
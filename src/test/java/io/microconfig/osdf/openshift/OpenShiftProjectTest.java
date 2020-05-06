package io.microconfig.osdf.openshift;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.microconfig.osdf.commandline.CommandLineOutput.errorOutput;
import static io.microconfig.osdf.commandline.CommandLineOutput.output;
import static io.microconfig.osdf.openshift.OpenShiftCredentials.*;
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
        commands.put("loginWithToken", "oc login url --token=oc-token");
        commands.put("logout", "oc logout");

        oc = mock(OCExecutor.class, withSettings().verboseLogging());
        when(oc.execute(commands.get("project")))
                .thenReturn(errorOutput("not a member", 1))
                .thenReturn(output("ok"));
        when(oc.execute(commands.get("login"))).thenReturn(output("ok"));
        when(oc.execute(commands.get("loginWithToken"))).thenReturn(output("ok"));

        project = new OpenShiftProject("url", "default", of("username:password"), oc);
        projectWithToken = new OpenShiftProject("url", "default", of("oc-token"), oc);
    }

    @Test
    void testConnect() {
        project.connect();
        verify(oc, times(2)).execute(commands.get("project"));
        verify(oc).execute(commands.get("login"));
    }

    @Test
    void testConnectWithToken() {
        projectWithToken.connect();
        verify(oc, times(2)).execute(commands.get("project"));
        verify(oc).execute(commands.get("loginWithToken"));
    }
}
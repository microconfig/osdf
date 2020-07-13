package io.osdf.core.connection.cli.openshift;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.osdf.core.connection.cli.CliOutput.output;
import static io.osdf.core.connection.cli.openshift.OpenShiftCredentials.of;
import static org.mockito.Mockito.*;

class OpenShiftAuthenticationTest {
    private OpenShiftCli oc;
    private final Map<String, String> commands = new HashMap<>();
    private OpenShiftAuthentication project;
    private OpenShiftAuthentication projectWithToken;

    @BeforeEach
    void setUp() {
        commands.put("login", "oc login url -u \"username\" -p \"password\" --insecure-skip-tls-verify");
        commands.put("loginWithToken", "oc login url --token=oc-token --insecure-skip-tls-verify");
        commands.put("project", "oc project default");
        commands.put("whoami", "oc whoami");
        commands.put("logout", "oc logout");

        oc = mock(OpenShiftCli.class);
        when(oc.execute(commands.get("whoami"))).thenReturn(output("not logged in"));
        when(oc.execute(commands.get("project"))).thenReturn(output("ok"));
        when(oc.execute(commands.get("login"))).thenReturn(output("ok"));
        when(oc.execute(commands.get("loginWithToken"))).thenReturn(output("ok"));

        project = new OpenShiftAuthentication("url", "default", of("username:password"), oc);
        projectWithToken = new OpenShiftAuthentication("url", "default", of("oc-token"), oc);
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
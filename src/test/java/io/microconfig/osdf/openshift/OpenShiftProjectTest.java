package io.microconfig.osdf.openshift;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OpenShiftProjectTest {
    private OCExecutor oc;
    private OpenShiftProject project;
    private Map<String, String> commands = new HashMap<>();

    @BeforeEach
    void setUp() {
        oc = mock(OCExecutor.class);
        project = new OpenShiftProject("url", "username", "password", "project", oc);

        commands.put("login", "oc login url -u username -p password");
        commands.put("project", "oc project project");
        commands.put("logout", "oc logout");
    }

    @Test
    void testConnect() {
        project.connect();
        verify(oc).execute(commands.get("login"));
        verify(oc).execute(commands.get("project"));
    }

    @Test
    void testLogout() {
        project.close();
        verify(oc).execute(commands.get("logout"));
    }
}
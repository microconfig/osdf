package io.microconfig.osdf.components;

import io.microconfig.osdf.openshift.OCExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static java.util.List.of;
import static org.mockito.Mockito.*;

class AbstractOpenShiftComponentTest {
    private OCExecutor oc;
    private DeploymentComponent component;
    private Map<String, String> commands = new HashMap<>();

    @BeforeEach
    void setUp() {
        oc = mock(OCExecutor.class);
        component = new DeploymentComponent("test-name", Path.of("/tmp/components/test-name"), Path.of("/tmp/components/test-name/openshift"), oc);

        commands.put("upload", "oc apply -f /tmp/components/test-name/openshift");
        commands.put("delete", "oc delete all,configmap --selector application=test-name");
        commands.put("createConfigMap", "oc create configmap test-name --from-file=/tmp/components/test-name");
        commands.put("labelConfigMap", "oc label configmap test-name application=test-name");

        when(oc.executeAndReadLines(commands.get("upload"))).thenReturn(of("resource1 configured", "resource2 configured"));
        when(oc.execute(commands.get("createConfigMap"))).thenReturn("created");
        when(oc.execute(commands.get("delete"))).thenReturn("deleted");
        when(oc.execute(commands.get("labelConfigMap"))).thenReturn("labeled");
    }

    @Test
    void testUpload() {
        component.upload();
        verify(oc).executeAndReadLines(commands.get("upload"));
    }

    @Test
    void testDelete() {
        component.delete();
        verify(oc).execute(commands.get("delete"));
    }

    @Test
    void testCreateConfigMap() {
        component.createConfigMap();
        verify(oc).execute(commands.get("createConfigMap"));
        verify(oc).execute(commands.get("labelConfigMap"));
    }
}
package io.microconfig.osdf.components;

import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.openshift.OCExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static java.util.List.of;
import static org.mockito.Mockito.*;

class AbstractOpenShiftComponentTest {
    private final Map<String, String> commands = new HashMap<>();
    private OCExecutor oc;
    private DeploymentComponent component;

    @BeforeEach
    void setUp() throws IOException {
        OSDFPaths paths = null; //TODO
        oc = mock(OCExecutor.class);
        component = new DeploymentComponent("helloworld-springboot", "latest", Path.of(paths.componentsPath() + "/helloworld-springboot"), oc);

        commands.put("upload", "oc apply -f " + paths.componentsPath() + "/helloworld-springboot/openshift");
        commands.put("delete", "oc delete all,configmap -l \"application in (helloworld-springboot), projectVersion in (latest)\"");
        commands.put("createConfigMap", "oc create configmap helloworld-springboot.latest --from-file=" + paths.componentsPath() + "/helloworld-springboot");
        commands.put("labelConfigMap", "oc label configmap helloworld-springboot.latest application=helloworld-springboot projectVersion=latest");

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
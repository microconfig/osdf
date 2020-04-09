package io.microconfig.osdf.components;

import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.Pod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.microconfig.osdf.utils.InstallInitUtils.createConfigsAndInstallInit;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class DeploymentComponentTest {
    private final Map<String, String> commands = new HashMap<>();
    private OCExecutor oc;
    private DeploymentComponent component;

    @BeforeEach
    void setUp() throws IOException {
        OSDFPaths paths = createConfigsAndInstallInit();
        oc = mock(OCExecutor.class);
        component = new DeploymentComponent("helloworld-springboot", "latest", Path.of(paths.componentsPath() + "/helloworld-springboot"), oc);

        commands.put("stop", "oc scale dc helloworld-springboot.latest --replicas=0");
        commands.put("pods", "oc get pods -l \"application in (helloworld-springboot), projectVersion in (latest)\" -o name");
        commands.put("upload", "oc apply -f " + paths.componentsPath() + "/helloworld-springboot/openshift");

        when(oc.execute(commands.get("stop"))).thenReturn("scaled");
        when(oc.executeAndReadLines(commands.get("upload"))).thenReturn(of("resource1 configured", "resource2 configured"));
    }

    @Test
    void testStop() {
        component.stop();
        verify(oc).execute(commands.get("stop"));
    }

    @Test
    void testPods() {
        when(oc.executeAndReadLines(commands.get("pods"))).thenReturn(of(
                "pod/pod1",
                "pod/pod2"
        ));

        List<Pod> pods = component.pods();
        verify(oc).executeAndReadLines(commands.get("pods"));
        assertEquals(2, pods.size());
        assertEquals("pod1", pods.get(0).getName());
        assertEquals("pod2", pods.get(1).getName());
    }

    @Test
    void testEmptyPods() {
        when(oc.executeAndReadLines(commands.get("pods"))).thenReturn(of());

        List<Pod> pods = component.pods();
        verify(oc).executeAndReadLines(commands.get("pods"));
        assertEquals(0, pods.size());
    }

    @Test
    void testGetPod() {
        when(oc.executeAndReadLines(commands.get("pods"))).thenReturn(of(
                "pod/pod1",
                "pod/pod2"
        ));

        Pod pod = component.pod("pod1");
        verify(oc).executeAndReadLines(commands.get("pods"));
        assertEquals("pod1", pod.getName());
    }

    @Test
    void testGetUnknownPod() {
        when(oc.executeAndReadLines(commands.get("pods"))).thenReturn(of(
                "pod/pod1",
                "pod/pod2"
        ));

        Pod pod = component.pod("pod3");
        verify(oc).executeAndReadLines(commands.get("pods"));
        assertNull(pod);
    }

    @Test
    void testRestart() {
        component.restart();
        verify(oc).execute(commands.get("stop"));
        verify(oc).executeAndReadLines(commands.get("upload"));
    }
}
package io.microconfig.osdf.components;

import io.microconfig.osdf.openshift.OpenShiftCLI;
import io.microconfig.osdf.openshift.Pod;
import io.microconfig.osdf.utils.TestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.microconfig.osdf.commandline.CommandLineOutput.output;
import static io.microconfig.osdf.openshift.Pod.fromPods;
import static io.microconfig.osdf.utils.TestContext.defaultContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DeploymentComponentTest {
    private final TestContext context = defaultContext();
    private final String COMPONENT_NAME = "helloworld-springboot";
    private final String COMPONENT_VERSION = "latest";

    private final Map<String, String> commands = new HashMap<>();
    private OpenShiftCLI oc;

    @Test
    void testStop() {
        component().stop();
        verify(oc).execute(commands.get("stop"));
    }

    @Test
    void testPods() {
        when(oc.execute(commands.get("pods"))).thenReturn(output(
                "pod/pod1" + "\n" +
                "pod/pod2"
        ));

        List<Pod> pods = component().pods();
        assertEquals(2, pods.size());
        assertEquals("pod1", pods.get(0).getName());
        assertEquals("pod2", pods.get(1).getName());
    }

    @Test
    void testEmptyPods() {
        when(oc.execute(commands.get("pods"))).thenReturn(output(""));

        List<Pod> pods = component().pods();
        assertEquals(0, pods.size());
    }

    @Test
    void testGetPod() {
        when(oc.execute(commands.get("pods"))).thenReturn(output(
                "pod/pod1" + "\n" +
                "pod/pod2"
        ));

        Pod pod = fromPods(component().pods(), "pod1");
        assertEquals("pod1", pod.getName());
    }

    @Test
    void testGetUnknownPod() {
        when(oc.execute(commands.get("pods"))).thenReturn(output(
                "pod/pod1" + "\n" +
                "pod/pod2"
        ));

        assertThrows(RuntimeException.class, () -> fromPods(component().pods(), "pod3"));
        verify(oc).execute(commands.get("pods"));
    }

    @BeforeEach
    void setUp() throws IOException {
        context.initDev();

        commands.put("stop", "oc scale dc " + COMPONENT_NAME + " --replicas=0");
        commands.put("pods", "oc get pods -l \"application in (" + COMPONENT_NAME + "), projectVersion in (" + COMPONENT_VERSION + ")\" -o name");
        commands.put("upload", "oc apply -f " + context.getPaths().componentsPath() + "/" + COMPONENT_NAME + "/openshift");

        oc = mock(OpenShiftCLI.class);
        when(oc.execute(commands.get("stop"))).thenReturn(output("scaled"));
        when(oc.execute(commands.get("upload"))).thenReturn(output("uploaded"));
    }

    private DeploymentComponent component() {
        return new DeploymentComponent(COMPONENT_NAME, COMPONENT_VERSION, Path.of(context.getPaths().componentsPath() + "/" + COMPONENT_NAME), oc);
    }
}
package io.cluster.old.cluster.openshift;

import io.cluster.old.cluster.pod.Pod;
import io.microconfig.osdf.exceptions.OSDFException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.cluster.old.cluster.commandline.CommandLineOutput.output;
import static io.cluster.old.cluster.pod.Pod.fromPods;
import static io.cluster.old.cluster.pod.Pod.pod;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PodTest {
    @Test
    void testDelete() {
        OpenShiftCLI oc = mock(OpenShiftCLI.class);
        when(oc.execute("delete pod pod")).thenReturn(output("deleted"));

        pod("pod", "component", oc).delete();
        verify(oc).execute("delete pod pod");
    }

    @Test
    void testFromPods() {
        OpenShiftCLI oc = mock(OpenShiftCLI.class);
        Pod pod1 = pod("pod1", "component", oc);
        Pod pod2 = pod("pod2", "component", oc);
        List<Pod> pods = of(pod1, pod2);
        assertEquals(pod1, fromPods(pods, null));
        assertEquals(pod2, fromPods(pods, "1"));
        assertEquals(pod1, fromPods(pods, "pod1"));

        assertThrows(OSDFException.class, () -> fromPods(pods, "2"));
        assertThrows(OSDFException.class, () -> fromPods(pods, "pod3"));
        assertThrows(OSDFException.class, () -> fromPods(of(), "0"));
    }
}
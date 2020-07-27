package io.osdf.core.cluster.pod;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.connection.cli.openshift.OpenShiftCli;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.osdf.core.cluster.pod.Pod.fromPods;
import static io.osdf.core.cluster.pod.Pod.pod;
import static io.osdf.core.connection.cli.CliOutput.output;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PodTest {
    @Test
    void testDelete() {
        OpenShiftCli oc = mock(OpenShiftCli.class);
        when(oc.execute("delete pod pod")).thenReturn(output("deleted"));

        pod("pod", oc).delete();
        verify(oc).execute("delete pod pod");
    }

    @Test
    void testFromPods() {
        OpenShiftCli oc = mock(OpenShiftCli.class);
        Pod pod1 = pod("pod1", oc);
        Pod pod2 = pod("pod2", oc);
        List<Pod> pods = of(pod1, pod2);
        assertEquals(pod1, fromPods(pods, null));
        assertEquals(pod2, fromPods(pods, "1"));
        assertEquals(pod1, fromPods(pods, "pod1"));

        assertThrows(OSDFException.class, () -> fromPods(pods, "2"));
        assertThrows(OSDFException.class, () -> fromPods(pods, "pod3"));
        assertThrows(OSDFException.class, () -> fromPods(of(), "0"));
    }
}
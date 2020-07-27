package io.osdf.core.cluster.pod;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.connection.cli.openshift.OpenShiftCli;
import io.osdf.test.cluster.api.PodApi;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static io.osdf.core.cluster.pod.Pod.fromPods;
import static io.osdf.core.cluster.pod.Pod.pod;
import static io.osdf.test.cluster.api.PodApi.podApi;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PodTest {
    @Test
    void testDelete() {
        PodApi podApi = podApi("test");
        pod("test", podApi).delete();
        assertFalse(podApi.exists());
    }

    @Test
    void testIsReady() {
        PodApi podApi = podApi("test");
        assertTrue(pod("test", podApi).isReady());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGettingContainers() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        PodApi podApi = podApi("test");
        Pod pod = pod("test", podApi);

        Method containers = pod.getClass().getDeclaredMethod("containers");
        containers.setAccessible(true);
        List<String> actual = (List<String>) containers.invoke(pod);
        assertEquals(of("first", "second"), actual);
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
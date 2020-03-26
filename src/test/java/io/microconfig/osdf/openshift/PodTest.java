package io.microconfig.osdf.openshift;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.microconfig.osdf.openshift.Pod.pod;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PodTest {
    @Test
    void testDelete() {
        OCExecutor oc = Mockito.mock(OCExecutor.class);
        when(oc.execute("oc delete pod pod")).thenReturn("deleted");

        pod("pod", "component", oc).delete();
        verify(oc).execute("oc delete pod pod");
    }
}
package io.microconfig.osdf.openshift;

import org.junit.jupiter.api.Test;

import static io.microconfig.osdf.commandline.CommandLineOutput.output;
import static io.microconfig.osdf.openshift.Pod.pod;
import static org.mockito.Mockito.*;

class PodTest {
    @Test
    void testDelete() {
        OCExecutor oc = mock(OCExecutor.class);
        when(oc.execute("oc delete pod pod")).thenReturn(output("deleted"));

        pod("pod", "component", oc).delete();
        verify(oc).execute("oc delete pod pod");
    }
}
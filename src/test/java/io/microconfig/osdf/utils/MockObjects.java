package io.microconfig.osdf.utils;

import io.cluster.old.cluster.openshift.OpenShiftCLI;

import static io.cluster.old.cluster.commandline.CommandLineOutput.output;
import static org.mockito.Mockito.*;

public class MockObjects {
    public static OpenShiftCLI loggedInOc() {
        OpenShiftCLI oc = mock(OpenShiftCLI.class);
        when(oc.execute("oc whoami")).thenReturn(output("user"));
        when(oc.execute("oc project default")).thenReturn(output("ok"));
        return oc;
    }
}

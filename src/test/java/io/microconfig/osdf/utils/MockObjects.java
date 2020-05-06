package io.microconfig.osdf.utils;

import io.microconfig.osdf.openshift.OCExecutor;

import static io.microconfig.osdf.commandline.CommandLineOutput.output;
import static org.mockito.Mockito.*;

public class MockObjects {
    public static OCExecutor loggedInOc() {
        OCExecutor oc = mock(OCExecutor.class);
        when(oc.execute("oc project unknown")).thenReturn(output("ok"));
        return oc;
    }
}

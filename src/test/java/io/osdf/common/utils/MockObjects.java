package io.osdf.common.utils;

import io.osdf.core.connection.cli.openshift.OpenShiftCli;

import static io.osdf.core.connection.cli.CliOutput.output;
import static org.mockito.Mockito.*;

public class MockObjects {
    public static OpenShiftCli loggedInOc() {
        OpenShiftCli oc = mock(OpenShiftCli.class);
        when(oc.execute("oc whoami")).thenReturn(output("user"));
        when(oc.execute("oc project default")).thenReturn(output("ok"));
        return oc;
    }
}

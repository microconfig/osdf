package io.microconfig.osdf.utils;

import io.microconfig.osdf.openshift.OCExecutor;

import static org.mockito.Mockito.*;

public class MockObjects {
    public static OCExecutor loggedInOc() {
        OCExecutor oc = mock(OCExecutor.class);
//        when(oc.execute("oc project crc", true)).thenReturn("ok");
        return oc;
    }
}

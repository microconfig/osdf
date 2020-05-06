package io.microconfig.osdf.components.info;

import io.microconfig.osdf.components.JobComponent;
import io.microconfig.osdf.openshift.OCExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.microconfig.osdf.components.info.JobInfo.jobInfo;
import static io.microconfig.osdf.components.info.JobStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class JobInfoTest {
    private OCExecutor oc;
    private JobComponent component;
    private String command;

    @BeforeEach
    void setUp() {
        oc = mock(OCExecutor.class);
        command = "oc get job test-name -o custom-columns=" +
                "failed:.status.failed," +
                "succeeded:.status.succeeded," +
                "active:.status.active," +
                "projectVersion:.metadata.labels.projectVersion," +
                "configVersion:.metadata.labels.configVersion";
        component = new JobComponent("test-name", null, null, oc);
    }

    @Test
    void jobSucceeded() {
//        when(oc.executeAndReadLines(command, true)).thenReturn(List.of(
//                "failed   succeeded   active   projectVersion   configVersion",
//                "0        1           0        latest           local"
//        ));
        assertEquals(SUCCEEDED, jobInfo(component.getName(), oc).getStatus());
    }

    @Test
    void jobFailed() {
//        when(oc.executeAndReadLines(command, true)).thenReturn(List.of(
//                "failed   succeeded   active   projectVersion   configVersion",
//                "1        0           0        latest           local"
//        ));
        assertEquals(FAILED, jobInfo(component.getName(), oc).getStatus());
    }

    @Test
    void jobActive() {
//        when(oc.executeAndReadLines(command, true)).thenReturn(List.of(
//                "failed   succeeded   active   projectVersion   configVersion",
//                "0        0           1        latest           local"
//        ));
        assertEquals(ACTIVE, jobInfo(component.getName(), oc).getStatus());
    }

    @Test
    void jobNotExecuted() {
//        when(oc.executeAndReadLines(command, true)).thenReturn(List.of(
//                "not found error"
//        ));
        assertEquals(NOT_EXECUTED, jobInfo(component.getName(), oc).getStatus());
    }

    @Test
    void badFormat() {
//        when(oc.executeAndReadLines(command, true)).thenReturn(List.of(
//                "failed   succeeded   active   projectVersion   configVersion",
//                "<none>   <none>      <none>   latest           local"
//        ));
        assertEquals(UNKNOWN, jobInfo(component.getName(), oc).getStatus());
    }
}
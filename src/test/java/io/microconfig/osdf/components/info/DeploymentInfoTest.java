package io.microconfig.osdf.components.info;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.components.checker.HealthChecker;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.Pod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static io.microconfig.osdf.components.info.DeploymentInfo.info;
import static io.microconfig.osdf.components.info.DeploymentStatus.*;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DeploymentInfoTest {
    private OCExecutor oc;
    private Pod pod1;
    private Pod pod2;
    private DeploymentComponent component;
    private String command;

    @BeforeEach
    void setUp() {
        oc = mock(OCExecutor.class);
        command = "oc get dc test-name.v1 -o custom-columns=" +
                "replicas:.spec.replicas," +
                "current:.status.replicas," +
                "available:.status.availableReplicas," +
                "unavailable:.status.unavailableReplicas," +
                "projectVersion:.metadata.labels.projectVersion," +
                "configVersion:.metadata.labels.configVersion";
        when(oc.executeAndReadLines(command, true)).thenReturn(of(
                "replicas   current   available   unavailable   projectVersion   configVersion",
                "2          2         2           0             latest           local"
        ));
        when(oc.executeAndReadLines("oc get pods -l \"application in (test-name), projectVersion in (v1)\" -o name")).thenReturn(of(
                "pod/pod1",
                "pod/pod2"
        ));
        pod1 = new Pod("pod1", "test-name", oc);
        pod2 = new Pod("pod2", "test-name", oc);
        component = new DeploymentComponent("test-name", "v1", Path.of("/tmp/components/test-name"), oc);
    }

    @Test
    void basicTest() {
        HealthChecker healthChecker = mock(HealthChecker.class);
        when(healthChecker.check(pod1)).thenReturn(true);
        when(healthChecker.check(pod2)).thenReturn(true);

        DeploymentInfo info = info(component, oc, healthChecker);
        assertEquals(RUNNING, info.getStatus());
        assertEquals(2, info.getAvailableReplicas());
        assertEquals(2, info.getReplicas());
        assertEquals("local", info.getConfigVersion());
        assertEquals("latest", info.getProjectVersion());
        assertEquals(of(true, true), info.getPodsHealth());
        assertEquals(of(pod1, pod2), info.getPods());
    }

    @Test
    void oneBadPod() {
        HealthChecker healthChecker = mock(HealthChecker.class);
        when(healthChecker.check(pod1)).thenReturn(true);
        when(healthChecker.check(pod2)).thenReturn(false);

        DeploymentInfo info = info(component, oc, healthChecker);
        assertEquals(BAD_HEALTHCHECK, info.getStatus());
        assertEquals(2, info.getAvailableReplicas());
        assertEquals(2, info.getReplicas());
        assertEquals("local", info.getConfigVersion());
        assertEquals("latest", info.getProjectVersion());
        assertEquals(of(true, false), info.getPodsHealth());
        assertEquals(of(pod1, pod2), info.getPods());
    }

    @Test
    void notFound() {
        when(oc.executeAndReadLines(command, true)).thenReturn(of(
                "not found error"
        ));
        HealthChecker healthChecker = mock(HealthChecker.class);
        DeploymentInfo info = info(component, oc, healthChecker);
        assertEquals(NOT_FOUND, info.getStatus());
    }

    @Test
    void badFormat() {
        when(oc.executeAndReadLines(command, true)).thenReturn(of(
                "replicas   current   available   unavailable   projectVersion   configVersion",
                "<none>     <none>    2           0             latest           local"
        ));
        HealthChecker healthChecker = mock(HealthChecker.class);
        DeploymentInfo info = info(component, oc, healthChecker);
        assertEquals(UNKNOWN, info.getStatus());
    }

    @Test
    void notEnoughReplicas() {
        when(oc.executeAndReadLines(command, true)).thenReturn(of(
                "replicas   current   available   unavailable   projectVersion   configVersion",
                "2          2         1           1             latest           local"
        ));
        HealthChecker healthChecker = mock(HealthChecker.class);
        DeploymentInfo info = info(component, oc, healthChecker);
        assertEquals(NOT_READY, info.getStatus());
    }
}
package io.microconfig.osdf.components.info;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.components.checker.HealthChecker;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.Pod;
import io.microconfig.osdf.paths.OSDFPaths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static io.microconfig.osdf.components.info.DeploymentInfo.info;
import static io.microconfig.osdf.components.info.DeploymentStatus.*;
import static io.microconfig.osdf.components.info.PodsHealthcheckInfo.podsInfo;
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
    void setUp() throws IOException {
        OSDFPaths paths = null; //TODO
        oc = mock(OCExecutor.class);
        command = "oc get dc helloworld-springboot.latest -o custom-columns=" +
                "replicas:.spec.replicas," +
                "current:.status.replicas," +
                "available:.status.availableReplicas," +
                "unavailable:.status.unavailableReplicas," +
                "projectVersion:.metadata.labels.projectVersion," +
                "configVersion:.metadata.labels.configVersion";
//        when(oc.executeAndReadLines(command, true)).thenReturn(of(
//                "replicas   current   available   unavailable   projectVersion   configVersion",
//                "2          2         2           0             latest           local"
//        ));
//        when(oc.executeAndReadLines("oc get pods -l \"application in (helloworld-springboot), projectVersion in (latest)\" -o name")).thenReturn(of(
//                "pod/pod1",
//                "pod/pod2"
//        ));
        pod1 = new Pod("pod1", "helloworld-springboot", oc);
        pod2 = new Pod("pod2", "helloworld-springboot", oc);
        component = new DeploymentComponent("helloworld-springboot", "latest", Path.of(paths.componentsPath() + "/helloworld-springboot"), oc);
    }

    @Test
    void basicTest() {
        HealthChecker healthChecker = mock(HealthChecker.class);
        when(healthChecker.check(pod1)).thenReturn(true);
        when(healthChecker.check(pod2)).thenReturn(true);

        DeploymentInfo info = info(component, oc);
        assertEquals(RUNNING, info.getStatus());
        assertEquals(2, info.getAvailableReplicas());
        assertEquals(2, info.getReplicas());
        assertEquals("local", info.getConfigVersion());
        assertEquals("latest", info.getProjectVersion());
    }

    @Test
    void oneBadPod() {
        HealthChecker healthChecker = mock(HealthChecker.class);
        when(healthChecker.check(pod1)).thenReturn(true);
        when(healthChecker.check(pod2)).thenReturn(false);

        DeploymentInfo info = info(component, oc);
        assertEquals(RUNNING, info.getStatus());
        assertEquals(2, info.getAvailableReplicas());
        assertEquals(2, info.getReplicas());
        assertEquals("local", info.getConfigVersion());
        assertEquals("latest", info.getProjectVersion());
        PodsHealthcheckInfo podsInfo = podsInfo(component);
        assertEquals(of(true, false), podsInfo.getPodsHealth());
        assertEquals(of(pod1, pod2), podsInfo.getPods());
    }

    @Test
    void notFound() {
//        when(oc.executeAndReadLines(command, true)).thenReturn(of(
//                "not found error"
//        ));
        DeploymentInfo info = info(component, oc);
        assertEquals(NOT_FOUND, info.getStatus());
    }

    @Test
    void badFormat() {
//        when(oc.executeAndReadLines(command, true)).thenReturn(of(
//                "replicas   current   available   unavailable   projectVersion   configVersion",
//                "<none>     <none>    2           0             latest           local"
//        ));
        DeploymentInfo info = info(component, oc);
        assertEquals(FAILED, info.getStatus());
    }

    @Test
    void notEnoughReplicas() {
//        when(oc.executeAndReadLines(command, true)).thenReturn(of(
//                "replicas   current   available   unavailable   projectVersion   configVersion",
//                "2          2         1           1             latest           local"
//        ));
        DeploymentInfo info = info(component, oc);
        assertEquals(NOT_READY, info.getStatus());
    }
}
package io.microconfig.osdf.deprecated.components.info;

import io.microconfig.osdf.deprecated.components.DeploymentComponent;
import io.microconfig.osdf.cluster.openshift.OpenShiftCLI;
import io.microconfig.osdf.utils.TestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static io.microconfig.osdf.commandline.CommandLineOutput.errorOutput;
import static io.microconfig.osdf.commandline.CommandLineOutput.output;
import static io.microconfig.osdf.deprecated.components.info.DeploymentInfo.info;
import static io.microconfig.osdf.service.deployment.info.DeploymentStatus.*;
import static io.microconfig.osdf.utils.MockObjects.loggedInOc;
import static io.microconfig.osdf.utils.OCCommands.deploymentInfoCustomColumns;
import static io.microconfig.osdf.utils.TestContext.defaultContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class DeploymentInfoTest {
    private final TestContext context = defaultContext();
    private final String COMPONENT_NAME = "helloworld-springboot";
    private final String COMPONENT_VERSION = "latest";

    private OpenShiftCLI oc;

    @Test
    void basicTest() {
        when(oc.execute(infoCommand())).thenReturn(output(
                "replicas   current   available   unavailable   projectVersion   configVersion  configHash" + "\n" +
                "2          2         2           0             latest           local          hash"
        ));
        DeploymentInfo info = info(component(), oc);
        assertEquals(RUNNING, info.getStatus());
        assertEquals(2, info.getAvailableReplicas());
        assertEquals(2, info.getReplicas());
        assertEquals("local", info.getConfigVersion());
        assertEquals("latest", info.getProjectVersion());
        assertEquals("hash", info.getHash());
    }

    @Test
    void notFound() {
        when(oc.execute(infoCommand())).thenReturn(errorOutput("not found error", 1));
        DeploymentInfo info = info(component(), oc);
        assertEquals(NOT_FOUND, info.getStatus());
    }

    @Test
    void badFormat() {
        when(oc.execute(infoCommand())).thenReturn(output(
                "replicas   current   available   unavailable   projectVersion   configVersion  configHash" + "\n" +
                "<none>     <none>    2           0             latest           local          hash"
        ));
        DeploymentInfo info = info(component(), oc);
        assertEquals(FAILED, info.getStatus());
    }

    @Test
    void notEnoughReplicas() {
        when(oc.execute(infoCommand())).thenReturn(output(
                "replicas   current   available   unavailable   projectVersion   configVersion  configHash" + "\n" +
                "2          2         1           1             latest           local          hash"
        ));
        DeploymentInfo info = info(component(), oc);
        assertEquals(NOT_READY, info.getStatus());
    }

    @BeforeEach
    void setUp() throws IOException {
        oc = loggedInOc();
        context.initDev();
    }

    private DeploymentComponent component() {
        return new DeploymentComponent(COMPONENT_NAME, COMPONENT_VERSION, Path.of(context.getPaths().componentsPath() + "/" + COMPONENT_NAME), oc);
    }

    private String infoCommand() {
        return "oc get dc helloworld-springboot " + deploymentInfoCustomColumns();
    }
}
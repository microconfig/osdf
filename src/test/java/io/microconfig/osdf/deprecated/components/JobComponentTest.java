package io.microconfig.osdf.deprecated.components;

import io.microconfig.osdf.cluster.openshift.OpenShiftCLI;
import io.microconfig.osdf.utils.TestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static io.microconfig.osdf.commandline.CommandLineOutput.output;
import static io.microconfig.osdf.utils.TestContext.defaultContext;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JobComponentTest {
    private final TestContext context = defaultContext();
    private final String JOB_NAME = "fakejob";
    private final String JOB_VERSION = "latest";

    private OpenShiftCLI oc;
    private final Map<String, String> commands = new HashMap<>();

    @Test
    void testExists() {
        when(oc.execute(commands.get("exists"))).thenReturn(output("exists"));
        assertTrue(component().exists());
    }

    @Test
    void testNotExists() {
        when(oc.execute(commands.get("exists"))).thenReturn(output("error"));
        assertFalse(component().exists());
    }

    @BeforeEach
    void setUp() throws IOException {
        context.initDev();

        oc = mock(OpenShiftCLI.class);
        commands.put("exists", "oc get job " + JOB_NAME);
    }

    private JobComponent component() {
        return new JobComponent(JOB_NAME, JOB_VERSION, Path.of(context.getPaths().componentsPath() + "/" + JOB_NAME), oc);
    }
}
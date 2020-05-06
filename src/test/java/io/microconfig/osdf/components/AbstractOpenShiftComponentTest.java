package io.microconfig.osdf.components;

import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.utils.TestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static io.microconfig.osdf.commandline.CommandLineOutput.output;
import static io.microconfig.osdf.utils.TestContext.defaultContext;
import static org.mockito.Mockito.*;

class AbstractOpenShiftComponentTest {
    private final TestContext context = defaultContext();
    private final String COMPONENT_NAME = "helloworld-springboot";
    private final String COMPONENT_VERSION = "latest";

    private final Map<String, String> commands = new HashMap<>();
    private OCExecutor oc;

    @Test
    void testUpload() {
        component().upload();
        verify(oc).execute(commands.get("upload"));
    }

    @Test
    void testDelete() {
        component().delete();
        verify(oc).execute(commands.get("delete"));
    }

    @Test
    void testCreateConfigMap() {
        component().createConfigMap();
        verify(oc).execute(commands.get("createConfigMap"));
        verify(oc).execute(commands.get("labelConfigMap"));
    }

    @BeforeEach
    void setUp() throws IOException {
        context.initDev();

        commands.put("upload", "oc apply -f " + context.getPaths().componentsPath() + "/" + COMPONENT_NAME + "/openshift");
        commands.put("delete", "oc delete all,configmap -l \"application in (" + COMPONENT_NAME + "), projectVersion in (" + COMPONENT_VERSION + ")\"");
        commands.put("createConfigMap", "oc create configmap " + COMPONENT_NAME + "." + COMPONENT_VERSION + " --from-file=" + context.getPaths().componentsPath() + "/" + COMPONENT_NAME);
        commands.put("labelConfigMap", "oc label configmap " + COMPONENT_NAME + "." + COMPONENT_VERSION + " application=helloworld-springboot projectVersion=latest");

        oc = mock(OCExecutor.class);
        when(oc.execute(commands.get("upload"))).thenReturn(output("ok"));
        when(oc.execute(commands.get("createConfigMap"))).thenReturn(output("created"));
        when(oc.execute(commands.get("delete"))).thenReturn(output("deleted"));
        when(oc.execute(commands.get("labelConfigMap"))).thenReturn(output("labeled"));
    }

    private DeploymentComponent component() {
        return new DeploymentComponent(COMPONENT_NAME, COMPONENT_VERSION, Path.of(context.getPaths().componentsPath() + "/" + COMPONENT_NAME), oc);
    }
}
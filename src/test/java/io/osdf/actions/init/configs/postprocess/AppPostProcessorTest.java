package io.osdf.actions.init.configs.postprocess;

import io.osdf.core.local.component.ComponentDir;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.osdf.actions.init.configs.postprocess.AppPostProcessor.componentPostProcessor;
import static io.osdf.test.ClasspathReader.classpathFile;
import static io.osdf.test.local.AppUtils.componentDirFor;
import static java.nio.file.Files.copy;
import static java.nio.file.Files.exists;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppPostProcessorTest {
    @Test
    void testSplitResources() throws IOException {
        ComponentDir componentDir = componentDirFor("simple-service");
        copy(classpathFile("resources/deployment-and-service.yaml"), componentDir.getPath("resources/deployment.yaml"), REPLACE_EXISTING);

        componentPostProcessor().process(componentDir);

        assertTrue(exists(componentDir.getPath("resources/deployment-Deployment-simple-service.yaml")));
        assertTrue(exists(componentDir.getPath("resources/deployment-Service-simple-service.yaml")));
        assertFalse(exists(componentDir.getPath("resources/deployment.yaml")));
    }

    @Test
    void testConfigMapIsCreated_WithoutResources() {
        ComponentDir componentDir = componentDirFor("configmap-plainApp");

        componentPostProcessor().process(componentDir);

        assertTrue(exists(componentDir.getPath("resources/configmap-app-configmap.yaml")));
    }
}
package io.osdf.actions.init.configs.postprocess;

import io.osdf.core.local.component.ComponentDir;
import io.osdf.core.local.component.MicroConfigComponentDir;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static io.osdf.actions.init.configs.postprocess.AppPostProcessor.componentPostProcessor;
import static io.osdf.core.local.component.MicroConfigComponentDir.componentDir;
import static io.osdf.test.ClasspathReader.classpathFile;
import static java.nio.file.Files.copy;
import static java.nio.file.Files.exists;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppPostProcessorTest {
    @TempDir
    Path tempDir;

    @Test
    void testSplitResources() throws IOException {
        ComponentDir componentDir = getComponentDir();
        copy(classpathFile("resources/deployment-and-service.yaml"), componentDir.getPath("resources/deployment.yaml"), REPLACE_EXISTING);

        componentPostProcessor().process(componentDir);

        assertTrue(exists(componentDir.getPath("resources/deployment-Deployment-simple-service.yaml")));
        assertTrue(exists(componentDir.getPath("resources/deployment-Service-simple-service.yaml")));
        assertFalse(exists(componentDir.getPath("resources/deployment.yaml")));
    }

    private MicroConfigComponentDir getComponentDir() throws IOException {
        Path serviceDir = classpathFile("components/simple-service");
        copyDirectory(serviceDir.toFile(), tempDir.toFile());

        return componentDir(tempDir);
    }
}
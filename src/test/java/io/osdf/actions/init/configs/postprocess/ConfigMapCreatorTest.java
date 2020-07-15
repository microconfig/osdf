package io.osdf.actions.init.configs.postprocess;

import io.osdf.core.local.component.ComponentDir;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static io.osdf.actions.init.configs.postprocess.ConfigMapCreator.configMapCreator;
import static io.osdf.core.local.component.MicroConfigComponentDir.componentDir;
import static io.osdf.test.ClasspathReader.classpathFile;
import static java.nio.file.Files.exists;
import static java.nio.file.Path.of;
import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigMapCreatorTest {
    @TempDir
    Path tempDir;

    @Test
    void createOneConfigMap() throws IOException {
        ComponentDir componentDir = getComponentDir();

        configMapCreator().createConfigMaps(componentDir);

        assertTrue(exists(of(tempDir + "/resources/configmap-simple-service.yaml")));
    }

    private ComponentDir getComponentDir() throws IOException {
        Path serviceDir = classpathFile("components/simple-service");
        copyDirectory(serviceDir.toFile(), tempDir.toFile());

        return componentDir(tempDir);
    }
}
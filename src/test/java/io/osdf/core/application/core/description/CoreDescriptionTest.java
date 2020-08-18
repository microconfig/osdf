package io.osdf.core.application.core.description;

import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.local.component.MicroConfigComponentDir;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static io.osdf.actions.init.configs.postprocess.metadata.MetadataCreatorImpl.metadataCreator;
import static io.osdf.actions.init.configs.postprocess.types.MetadataType.SERVICE;
import static io.osdf.core.application.core.description.CoreDescription.from;
import static io.osdf.core.application.core.files.ApplicationFilesImpl.applicationFiles;
import static io.osdf.core.local.component.MicroConfigComponentDir.componentDir;
import static io.osdf.test.ClasspathReader.classpathFile;
import static java.util.List.of;
import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CoreDescriptionTest {
    @TempDir
    Path tempDir;

    @Test
    void serviceDescription() throws IOException {
        ApplicationFiles applicationFiles = getApplicationFiles();

        CoreDescription description = from(applicationFiles);

        assertEquals("latest", description.getAppVersion());
        assertEquals("master", description.getConfigVersion());
        assertEquals("SERVICE", description.getType());
        assertEquals(of("deployment/simple-service"), description.getResources());
    }

    private ApplicationFiles getApplicationFiles() throws IOException {
        Path serviceDir = classpathFile("components/simple-service");
        copyDirectory(serviceDir.toFile(), tempDir.toFile());

        MicroConfigComponentDir componentDir = componentDir(tempDir);
        ApplicationFiles files = applicationFiles(componentDir);
        metadataCreator().create(componentDir);
        return files;
    }
}
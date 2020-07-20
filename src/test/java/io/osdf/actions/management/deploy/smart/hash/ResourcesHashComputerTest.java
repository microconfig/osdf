package io.osdf.actions.management.deploy.smart.hash;

import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.local.component.MicroConfigComponentDir;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static io.osdf.actions.init.configs.postprocess.metadata.MetadataCreatorImpl.metadataCreator;
import static io.osdf.actions.init.configs.postprocess.types.MetadataType.SERVICE;
import static io.osdf.actions.management.deploy.smart.hash.ResourcesHashComputer.resourcesHashComputer;
import static io.osdf.core.application.core.files.ApplicationFilesImpl.applicationFiles;
import static io.osdf.core.local.component.MicroConfigComponentDir.componentDir;
import static io.osdf.test.ClasspathReader.classpathFile;
import static java.nio.file.Files.readString;
import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ResourcesHashComputerTest {
    @TempDir
    Path tempDir;

    @Test
    void hashInserted() throws IOException {
        ApplicationFiles files = getApplicationFiles(true);

        resourcesHashComputer().insertIn(files);

        assertFalse(readString(files.getPath("resources/deployment.yaml")).contains("<CONFIG_HASH>"));
    }

    @Test
    void skipIfHashInsertionIsNotNeeded() throws IOException {
        ApplicationFiles files = getApplicationFiles(false);
        String originalContent = readString(files.getPath("resources/deployment.yaml"));

        resourcesHashComputer().insertIn(files);

        String contentAfterInsertion = readString(files.getPath("resources/deployment.yaml"));
        assertEquals(originalContent, contentAfterInsertion);
    }

    private ApplicationFiles getApplicationFiles(boolean withHash) throws IOException {
        Path serviceDir = classpathFile("components/" + (withHash ? "simple-service" : "without-smart"));
        copyDirectory(serviceDir.toFile(), tempDir.toFile());

        MicroConfigComponentDir componentDir = componentDir(tempDir);
        ApplicationFiles files = applicationFiles(componentDir);
        metadataCreator().create(SERVICE, componentDir);
        return files;
    }
}
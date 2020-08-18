package io.osdf.actions.management.deploy.smart.hash;

import io.osdf.core.application.core.files.ApplicationFiles;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.osdf.actions.management.deploy.smart.hash.ResourcesHashComputer.resourcesHashComputer;
import static io.osdf.test.local.AppUtils.applicationFilesFor;
import static java.nio.file.Files.readString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ResourcesHashComputerTest {
    @Test
    void hashInserted() throws IOException {
        ApplicationFiles files = applicationFilesFor("simple-service");

        resourcesHashComputer().insertIn(files);

        assertFalse(readString(files.getPath("resources/deployment.yaml")).contains("<CONFIG_HASH>"));
    }

    @Test
    void skipIfHashInsertionIsNotNeeded() throws IOException {
        ApplicationFiles files = applicationFilesFor("without-smart");
        String originalContent = readString(files.getPath("resources/deployment.yaml"));

        resourcesHashComputer().insertIn(files);

        String contentAfterInsertion = readString(files.getPath("resources/deployment.yaml"));
        assertEquals(originalContent, contentAfterInsertion);
    }
}
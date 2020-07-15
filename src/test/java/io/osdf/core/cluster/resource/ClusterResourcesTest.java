package io.osdf.core.cluster.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static io.osdf.core.cluster.resource.ClusterResourceImpl.*;
import static io.osdf.core.cluster.resource.LocalClusterResourceImpl.localClusterResource;
import static java.nio.file.Files.writeString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClusterResourcesTest {
    @TempDir
    Path tempDir;

    @Test
    void testCreationFromOpenShiftNotation() {
        ClusterResourceImpl resource = fromOpenShiftNotation("Deployment/Example");

        assertEquals("deployment", resource.kind());
        assertEquals("Example", resource.name());
    }

    @Test
    void testCreationFromPath() throws IOException {
        Path pathToResource = createLocalResource();

        ClusterResourceImpl resource = fromPath(pathToResource);

        assertEquals("deployment", resource.kind());
        assertEquals("Example", resource.name());
    }

    @Test
    void testEqualsBetweenResourceImplementations() throws IOException {
        ClusterResourceImpl resource = clusterResource("Deployment", "Example");
        LocalClusterResourceImpl localResource = localClusterResource(createLocalResource());

        assertTrue(localResource.equals(resource));
        assertTrue(resource.equals(localResource));
    }

    @Test
    void checkPathParsedCorrectly() throws IOException {
        Path resourcePath = createLocalResource();
        LocalClusterResourceImpl localResource = localClusterResource(resourcePath);

        assertEquals(resourcePath, localResource.path());
    }

    private Path createLocalResource() throws IOException {
        Path pathToResource = tempDir.resolve("resource.yaml");
        writeString(pathToResource, "kind: Deployment" + "\n" +
                "metadata:" + "\n" +
                "  name: Example");
        return pathToResource;
    }
}
package io.osdf.core.cluster.resource;

import io.osdf.test.cluster.ResourceApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static io.osdf.core.cluster.resource.ClusterResourceImpl.*;
import static io.osdf.core.cluster.resource.LocalClusterResourceImpl.localClusterResource;
import static io.osdf.test.cluster.ResourceApi.resourceApi;
import static java.nio.file.Files.writeString;
import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void updateResource() throws IOException {
        ResourceApi resourceApi = resourceApi("deployment", "Example");
        int currentVersion = resourceApi.resourceVersion();
        LocalClusterResourceImpl resource = localClusterResource(createLocalResource());

        resource.upload(resourceApi);

        assertNotEquals(currentVersion, resourceApi.resourceVersion());
    }

    @Test
    void ifImmutableFieldHasChanged_ResourceIsUpdated() throws IOException {
        ResourceApi resourceApi = resourceApi("deployment", "Example")
                .expectImmutableChange();
        int originalVersion = resourceApi.resourceVersion();
        LocalClusterResourceImpl resource = localClusterResource(createLocalResource());

        resource.upload(resourceApi);

        assertNotEquals(originalVersion, resourceApi.resourceVersion());
    }

    @Test
    void testExistsAndDelete() throws IOException {
        ResourceApi resourceApi = resourceApi("deployment", "Example");
        LocalClusterResourceImpl resource = localClusterResource(createLocalResource());

        assertTrue(resource.exists(resourceApi));

        resource.delete(resourceApi);
        assertFalse(resource.exists(resourceApi));
    }

    private Path createLocalResource() throws IOException {
        Path pathToResource = tempDir.resolve("resource.yaml");
        writeString(pathToResource, "kind: Deployment" + "\n" +
                "metadata:" + "\n" +
                "  name: Example");
        return pathToResource;
    }
}
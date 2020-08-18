package io.osdf.actions.management.deploy.deployer;

import io.osdf.core.application.core.description.CoreDescription;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.test.cluster.api.ResourceApi;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.osdf.actions.management.deploy.deployer.ResourceDeleter.resourceDeleter;
import static io.osdf.test.cluster.api.ResourceApi.resourceApi;
import static io.osdf.test.local.AppUtils.applicationFilesFor;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ResourceDeleterTest {
    @Test
    void deleteOldResources() {
        ApplicationFiles files = applicationFilesFor("simple-service");
        ResourceApi resourceApi = resourceApi("configmap", "old");
        CoreDescription coreDescription = withResources(of("deployment/simple-service", "configmap/old"));

        resourceDeleter(resourceApi).deleteOldResources(coreDescription, files);

        assertFalse(resourceApi.exists());
    }

    @Test
    void deleteConfigMaps() {
        ResourceApi resourceApi = resourceApi("configmap", "old");
        CoreDescription coreDescription = withResources(of("configmap/old"));

        resourceDeleter(resourceApi).deleteConfigMaps(coreDescription);

        assertFalse(resourceApi.exists());
    }

    private CoreDescription withResources(List<String> strings) {
        return new CoreDescription("1", "1", strings, "SERVICE");
    }
}
package io.osdf.actions.management.deploy.deployer;

import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.test.cluster.api.ResourceApi;
import org.junit.jupiter.api.Test;

import static io.osdf.actions.management.deploy.deployer.ImmutableAwareUploader.immutableAwareUploader;
import static io.osdf.test.cluster.api.ResourceApi.resourceApi;
import static io.osdf.test.local.AppUtils.applicationFilesFor;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ImmutableAwareUploaderTest {
    @Test
    void successUpload_ifResourceIsImmutable() {
        assertResourceUpload(resourceApi("deployment", "simple-service").expectImmutableChange());
    }

    @Test
    void successUpload() {
        assertResourceUpload(resourceApi("deployment", "simple-service"));
    }

    private void assertResourceUpload(ResourceApi resourceApi) {
        int originalVersion = resourceApi.resourceVersion();
        ApplicationFiles files = applicationFilesFor("simple-service");

        immutableAwareUploader(resourceApi).uploadResources(files);

        assertNotEquals(originalVersion, resourceApi.resourceVersion());
    }
}
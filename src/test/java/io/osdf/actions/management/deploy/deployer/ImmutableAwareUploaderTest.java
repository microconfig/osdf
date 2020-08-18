package io.osdf.actions.management.deploy.deployer;

import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.local.component.MicroConfigComponentDir;
import io.osdf.test.cluster.api.ResourceApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static io.osdf.actions.init.configs.postprocess.metadata.MetadataCreatorImpl.metadataCreator;
import static io.osdf.actions.management.deploy.deployer.ImmutableAwareUploader.immutableAwareUploader;
import static io.osdf.core.application.core.files.ApplicationFilesImpl.applicationFiles;
import static io.osdf.core.local.component.MicroConfigComponentDir.componentDir;
import static io.osdf.test.ClasspathReader.classpathFile;
import static io.osdf.test.cluster.api.ResourceApi.resourceApi;
import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ImmutableAwareUploaderTest {
    @TempDir
    Path tempDir;

    @Test
    void successUpload_ifResourceIsImmutable() throws IOException {
        assertResourceUpload(resourceApi("deployment", "simple-service").expectImmutableChange());
    }

    @Test
    void successUpload() throws IOException {
        assertResourceUpload(resourceApi("deployment", "simple-service"));
    }

    private void assertResourceUpload(ResourceApi resourceApi) throws IOException {
        int originalVersion = resourceApi.resourceVersion();
        ApplicationFiles files = getApplicationFiles();

        immutableAwareUploader(resourceApi).uploadResources(files);

        assertNotEquals(originalVersion, resourceApi.resourceVersion());
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
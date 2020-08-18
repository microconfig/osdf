package io.osdf.actions.management.deploy.deployer;

import io.osdf.core.application.core.description.CoreDescription;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.local.component.MicroConfigComponentDir;
import io.osdf.test.cluster.api.ResourceApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static io.osdf.actions.init.configs.postprocess.metadata.MetadataCreatorImpl.metadataCreator;
import static io.osdf.actions.management.deploy.deployer.ResourceDeleter.resourceDeleter;
import static io.osdf.core.application.core.files.ApplicationFilesImpl.applicationFiles;
import static io.osdf.core.local.component.MicroConfigComponentDir.componentDir;
import static io.osdf.test.ClasspathReader.classpathFile;
import static io.osdf.test.cluster.api.ResourceApi.resourceApi;
import static java.util.List.of;
import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ResourceDeleterTest {
    @TempDir
    Path tempDir;

    @Test
    void deleteOldResources() throws IOException {
        ApplicationFiles files = getApplicationFiles();
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

    private ApplicationFiles getApplicationFiles() throws IOException {
        Path serviceDir = classpathFile("components/simple-service");
        copyDirectory(serviceDir.toFile(), tempDir.toFile());

        MicroConfigComponentDir componentDir = componentDir(tempDir);
        ApplicationFiles files = applicationFiles(componentDir);
        metadataCreator().create(componentDir);
        return files;
    }
}
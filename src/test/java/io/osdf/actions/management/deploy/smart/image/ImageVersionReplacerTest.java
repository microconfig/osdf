package io.osdf.actions.management.deploy.smart.image;

import io.osdf.actions.management.deploy.smart.image.digest.DigestGetter;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.local.component.MicroConfigComponentDir;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static io.osdf.actions.init.configs.postprocess.metadata.MetadataCreatorImpl.metadataCreator;
import static io.osdf.actions.init.configs.postprocess.types.MetadataType.SERVICE;
import static io.osdf.actions.management.deploy.smart.image.ImageVersionReplacer.imageVersionReplacer;
import static io.osdf.core.application.core.files.ApplicationFilesImpl.applicationFiles;
import static io.osdf.core.local.component.MicroConfigComponentDir.componentDir;
import static io.osdf.test.ClasspathReader.classpathFile;
import static java.nio.file.Files.readString;
import static java.nio.file.Path.of;
import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ImageVersionReplacerTest {
    @TempDir
    Path tempDir;

    @Test
    void replaceNeeded() throws IOException {
        ImageVersionReplacer replacer = imageVersionReplacer(digestGetter());
        ApplicationFiles files = getApplicationFiles(true);

        replacer.replaceFor(files);

        assertTrue(readString(of(tempDir + "/resources/deployment.yaml")).contains("@sha256:digest"));
    }

    @Test
    void replaceNotNeeded() throws IOException {
        ImageVersionReplacer replacer = imageVersionReplacer(digestGetter());
        ApplicationFiles files = getApplicationFiles(false);

        replacer.replaceFor(files);

        assertFalse(readString(of(tempDir + "/resources/deployment.yaml")).contains("@sha256:digest"));
        assertTrue(readString(of(tempDir + "/resources/deployment.yaml")).contains(":latest"));
    }

    @Test
    void digestNotAvailable() throws IOException {
        ImageVersionReplacer replacer = imageVersionReplacer(faultyDigestGetter());
        ApplicationFiles files = getApplicationFiles(true);

        assertThrows(OSDFException.class, () -> replacer.replaceFor(files));
    }

    private ApplicationFiles getApplicationFiles(boolean withDigest) throws IOException {
        Path serviceDir = classpathFile("components/" + (withDigest ? "simple-service" : "without-smart"));
        copyDirectory(serviceDir.toFile(), tempDir.toFile());

        MicroConfigComponentDir componentDir = componentDir(tempDir);
        ApplicationFiles files = applicationFiles(componentDir);
        metadataCreator().create(componentDir);
        return files;
    }

    private DigestGetter digestGetter() {
        DigestGetter digestGetter = mock(DigestGetter.class);
        when(digestGetter.get(anyString())).thenReturn("sha256:digest");
        return digestGetter;
    }

    private DigestGetter faultyDigestGetter() {
        DigestGetter digestGetter = mock(DigestGetter.class);
        when(digestGetter.get(anyString())).thenThrow(new OSDFException());
        return digestGetter;
    }
}
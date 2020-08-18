package io.osdf.actions.management.deploy.smart.image;

import io.osdf.actions.management.deploy.smart.image.digest.DigestGetter;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.core.files.ApplicationFiles;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.osdf.actions.management.deploy.smart.image.ImageVersionReplacer.imageVersionReplacer;
import static io.osdf.test.local.AppUtils.applicationFilesFor;
import static java.nio.file.Files.readString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ImageVersionReplacerTest {
    @Test
    void replaceNeeded() throws IOException {
        ImageVersionReplacer replacer = imageVersionReplacer(digestGetter());
        ApplicationFiles files = applicationFilesFor("simple-service");

        replacer.replaceFor(files);

        assertTrue(readString(files.getPath("resources/deployment.yaml")).contains("@sha256:digest"));
    }

    @Test
    void replaceNotNeeded() throws IOException {
        ImageVersionReplacer replacer = imageVersionReplacer(digestGetter());
        ApplicationFiles files = applicationFilesFor("without-smart");

        replacer.replaceFor(files);

        assertFalse(readString(files.getPath("resources/deployment.yaml")).contains("@sha256:digest"));
        assertTrue(readString(files.getPath("resources/deployment.yaml")).contains(":latest"));
    }

    @Test
    void digestNotAvailable() {
        ImageVersionReplacer replacer = imageVersionReplacer(faultyDigestGetter());
        ApplicationFiles files = applicationFilesFor("simple-service");

        assertThrows(OSDFException.class, () -> replacer.replaceFor(files));
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
package io.osdf.actions.management.deploy.smart.image;

import io.osdf.actions.management.deploy.smart.image.digest.DigestGetter;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.local.component.MicroConfigComponentDir;
import org.junit.jupiter.api.Test;

import static io.osdf.actions.init.configs.postprocess.metadata.MetadataCreatorImpl.metadataCreator;
import static io.osdf.actions.init.configs.postprocess.types.MetadataType.SERVICE;
import static io.osdf.actions.management.deploy.smart.image.ImageVersionReplacer.imageVersionReplacer;
import static io.osdf.core.application.core.files.ApplicationFilesImpl.applicationFiles;
import static io.osdf.core.local.component.MicroConfigComponentDir.componentDir;
import static io.osdf.test.ClasspathReader.classpathFile;
import static io.osdf.test.ClasspathReader.read;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ImageVersionReplacerTest {
    @Test
    void replaceNeeded() {
        ImageVersionReplacer replacer = imageVersionReplacer(digestGetter());
        ApplicationFiles files = getApplicationFiles();

        replacer.replaceFor(files);

        assertTrue(read("components/simple-service/resources/deployment.yaml").contains("@sha256:digest"));
    }

    @Test
    void replaceNotNeeded() {
        ImageVersionReplacer replacer = imageVersionReplacer(digestGetter());
        ApplicationFiles files = getApplicationFiles();

        replacer.replaceFor(files);

        assertFalse(read("components/without-digest/resources/deployment.yaml").contains("@sha256:digest"));
        assertTrue(read("components/without-digest/resources/deployment.yaml").contains(":latest"));
    }

    @Test
    void digestNotAvailable() {
        ImageVersionReplacer replacer = imageVersionReplacer(faultyDigestGetter());
        ApplicationFiles files = getApplicationFiles();

        assertThrows(OSDFException.class, () -> replacer.replaceFor(files));
    }

    private ApplicationFiles getApplicationFiles() {
        MicroConfigComponentDir componentDir = componentDir(classpathFile("components/simple-service"));
        ApplicationFiles files = applicationFiles(componentDir);
        metadataCreator().create(SERVICE, componentDir);
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
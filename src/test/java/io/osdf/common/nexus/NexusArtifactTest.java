package io.osdf.common.nexus;

import io.osdf.common.exceptions.OSDFException;
import org.junit.jupiter.api.Test;

import static io.osdf.common.nexus.NexusArtifact.configsNexusArtifact;
import static org.junit.jupiter.api.Assertions.*;

class NexusArtifactTest {
    @Test
    void correctConfigDownloadUrl_withClassifier() {
        String downloadUrl = configsNexusArtifact("group:artifact:version:classifier").getDownloadUrl("url.ru/repo");
        assertEquals("url.ru/repo/group/artifact/version/artifact-version-classifier.zip", downloadUrl);
    }

    @Test
    void correctConfigDownloadUrl_withoutClassifier() {
        String downloadUrl = configsNexusArtifact("group:artifact:version").getDownloadUrl("url.ru/repo");
        assertEquals("url.ru/repo/group/artifact/version/artifact-version.zip", downloadUrl);
    }

    @Test
    void IfBadFormatOfArtifactString_throwException() {
        assertThrows(OSDFException.class, () -> configsNexusArtifact("one"));
        assertThrows(OSDFException.class, () -> configsNexusArtifact("one:two"));
        assertThrows(OSDFException.class, () -> configsNexusArtifact("one:two:three:four:five"));
    }

    @Test
    void correctToString() {
        assertEquals("group:artifact:version:classifier", configsNexusArtifact("group:artifact:version:classifier").toString());
        assertEquals("group:artifact:version", configsNexusArtifact("group:artifact:version").toString());
    }
}
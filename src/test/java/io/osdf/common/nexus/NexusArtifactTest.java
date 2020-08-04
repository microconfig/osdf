package io.osdf.common.nexus;

import org.junit.jupiter.api.Test;

import static io.osdf.common.nexus.NexusArtifact.configsNexusArtifact;
import static org.junit.jupiter.api.Assertions.*;

class NexusArtifactTest {
    @Test
    void correctConfigDownloadUrl_withClassifier() {
        String downloadUrl = configsNexusArtifact("group:artifact:version:classifier").getDownloadUrl("url.ru/repo");
        assertEquals("url.ru/repo/group/artifact/version/artifact-version-classifier.zip", downloadUrl);
    }
}
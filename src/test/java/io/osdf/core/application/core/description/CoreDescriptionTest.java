package io.osdf.core.application.core.description;

import io.osdf.core.application.core.files.ApplicationFiles;
import org.junit.jupiter.api.Test;

import static io.osdf.core.application.core.description.CoreDescription.from;
import static io.osdf.test.local.AppUtils.applicationFilesFor;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CoreDescriptionTest {
    @Test
    void serviceDescription() {
        ApplicationFiles applicationFiles = applicationFilesFor("simple-service");

        CoreDescription description = from(applicationFiles);

        assertEquals("latest", description.getAppVersion());
        assertEquals("master", description.getConfigVersion());
        assertEquals("SERVICE", description.getType());
        assertEquals(of("deployment/simple-service"), description.getResources());
    }
}
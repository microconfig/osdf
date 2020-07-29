package io.osdf.core.application.core;

import io.osdf.core.application.core.description.CoreDescription;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.test.cluster.api.ConfigMapApi;
import io.osdf.test.cluster.api.ServiceApi;
import org.junit.jupiter.api.Test;

import static io.osdf.core.application.core.AbstractApplication.application;
import static io.osdf.core.application.core.AbstractApplication.remoteApplication;
import static io.osdf.test.cluster.api.ConfigMapApi.configMapApi;
import static io.osdf.test.cluster.api.ServiceApi.serviceApi;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AbstractApplicationTest {
    @Test
    void ifConfigMapDoesntExist_OptionalIsEmpty() {
        ConfigMapApi configMapApi = configMapApi("test-osdf");
        configMapApi.exists(false);

        ApplicationFiles files = mock(ApplicationFiles.class);
        when(files.name()).thenReturn("test");

        assertTrue(application(configMapApi, files)
                .loadDescription(CoreDescription.class, "core")
                .isEmpty());
    }

    @Test
    void testDelete() {
        ServiceApi serviceApi = serviceApi("test-app");
        AbstractApplication app = remoteApplication("test-app", serviceApi);

        app.delete();

        assertTrue(serviceApi.isDeleted());
    }
}
package io.osdf.core.application.core;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.core.description.CoreDescription;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.test.cluster.api.ConfigMapApi;
import io.osdf.test.cluster.api.ServiceApi;
import org.junit.jupiter.api.Test;

import static io.osdf.core.application.core.AbstractApplication.application;
import static io.osdf.core.application.core.AbstractApplication.remoteApplication;
import static io.osdf.test.cluster.api.ConfigMapApi.configMapApi;
import static io.osdf.test.cluster.api.ServiceApi.serviceApi;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AbstractApplicationTest {
    @Test
    void ifConfigMapDoesntExist_throwOsdfException() {
        ConfigMapApi configMapApi = configMapApi("test-osdf");
        configMapApi.exists(false);

        ApplicationFiles files = mock(ApplicationFiles.class);
        when(files.name()).thenReturn("test");

        assertThrows(OSDFException.class, () ->
                application(configMapApi, files).loadDescription(CoreDescription.class, "core"));
    }

    @Test
    void testDelete() {
        ServiceApi serviceApi = serviceApi("test-app");
        AbstractApplication app = remoteApplication("test-app", serviceApi);

        app.delete();

        assertTrue(serviceApi.isDeleted());
    }
}
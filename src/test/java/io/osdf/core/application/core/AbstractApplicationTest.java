package io.osdf.core.application.core;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.core.description.CoreDescription;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.test.cluster.api.ConfigMapApi;
import org.junit.jupiter.api.Test;

import static io.osdf.core.application.core.AbstractApplication.application;
import static io.osdf.test.cluster.api.ConfigMapApi.configMapApi;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
}
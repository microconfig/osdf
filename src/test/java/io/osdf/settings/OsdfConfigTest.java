package io.osdf.settings;

import io.microconfig.core.properties.repository.ComponentNotFoundException;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.local.microconfig.property.PropertyGetter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OsdfConfigTest {
    @Test
    void throw_OSDFException_ifPropertiesComponentDoesntExist() {
        PropertyGetter getter = mock(PropertyGetter.class);
        when(getter.get(any(), any(), any())).thenThrow(new ComponentNotFoundException("osdf-config"));

        OsdfConfig config = new OsdfConfig(getter);
        assertThrows(OSDFException.class, config::group);
    }

    @Test
    void returnNull_ifPropertyDoesntExist() {
        PropertyGetter getter = mock(PropertyGetter.class);
        when(getter.get(any(), any(), any())).thenThrow(new IllegalArgumentException());

        OsdfConfig config = new OsdfConfig(getter);
        assertNull(config.maxParallel());
    }
}
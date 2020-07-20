package io.osdf.core.connection.context;

import io.microconfig.core.properties.repository.ComponentNotFoundException;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.local.microconfig.property.PropertyGetter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClusterPropertiesTest {
    @Test
    void throw_OSDFException_ifPropertiesComponentDoesntExist() {
        PropertyGetter getter = mock(PropertyGetter.class);
        when(getter.get(any(), any(), any())).thenThrow(new ComponentNotFoundException("k8s-cluster"));

        ClusterProperties clusterProperties = new ClusterProperties(getter);
        assertThrows(OSDFException.class, clusterProperties::clusterUrl);
    }
}
package io.osdf.core.cluster.configmap;

import io.osdf.test.cluster.api.ConfigMapApi;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.osdf.core.cluster.configmap.ConfigMapLoader.configMapLoader;
import static io.osdf.test.cluster.api.ConfigMapApi.configMapApi;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigMapLoaderTest {
    @Getter @Setter
    private static class SomeDescription {
        private String value;
    }

    @Test
    void updateDescription() {
        ConfigMapApi configMapApi = configMapApi("name");
        ConfigMapLoader uploader = configMapLoader(configMapApi);
        int originalVersion = configMapApi.resourceVersion();

        uploader.upload("name", Map.of(
                "description", new SomeDescription()
        ));

        assertNotEquals(originalVersion, configMapApi.resourceVersion());
    }

    @Test
    void createNewDescription() {
        ConfigMapApi configMapApi = configMapApi("name");
        configMapApi.exists(false);

        ConfigMapLoader uploader = configMapLoader(configMapApi);

        uploader.upload("name", Map.of(
                "description", new SomeDescription()
        ));

        assertTrue(configMapApi.exists());
    }
}
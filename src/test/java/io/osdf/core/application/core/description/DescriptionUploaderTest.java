package io.osdf.core.application.core.description;

import io.osdf.test.cluster.ConfigMapApi;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.osdf.core.application.core.description.DescriptionUploader.descriptionUploader;
import static io.osdf.test.cluster.ConfigMapApi.configMapApi;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DescriptionUploaderTest {
    @Getter @Setter
    private static class SomeDescription {
        private String value;
    }

    @Test
    void updateDescription() {
        ConfigMapApi configMapApi = configMapApi("name");
        DescriptionUploader uploader = descriptionUploader(configMapApi);
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

        DescriptionUploader uploader = descriptionUploader(configMapApi);

        uploader.upload("name", Map.of(
                "description", new SomeDescription()
        ));

        assertTrue(configMapApi.exists());
    }
}
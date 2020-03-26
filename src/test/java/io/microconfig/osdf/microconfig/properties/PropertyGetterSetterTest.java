package io.microconfig.osdf.microconfig.properties;

import io.microconfig.osdf.utils.ConfigUnzipper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static io.microconfig.factory.configtypes.StandardConfigTypes.PROCESS;
import static io.microconfig.osdf.microconfig.properties.PropertyGetter.propertyGetter;
import static io.microconfig.osdf.microconfig.properties.PropertySetter.propertySetter;
import static java.nio.file.Path.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PropertyGetterSetterTest {
    private Path configsPath = of("/tmp/configs");


    @BeforeEach
    void createConfigs() throws IOException {
        ConfigUnzipper.unzip("configs.zip", configsPath);
    }

    @Test
    void getSetGet() {
        String oldVersion = propertyGetter("dev", of(configsPath + "/repo")).get(PROCESS, "versions", "project.version");
        assertEquals("latest", oldVersion);

        propertySetter().setIfNecessary(of(configsPath + "/repo/components/system/versions/project-version.proc"), "project.version", "v2");

        String newVersion = propertyGetter("dev", of(configsPath + "/repo")).get(PROCESS, "versions", "project.version");
        assertEquals("v2", newVersion);
    }
}
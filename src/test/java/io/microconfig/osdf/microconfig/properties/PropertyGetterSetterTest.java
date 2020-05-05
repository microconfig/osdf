package io.microconfig.osdf.microconfig.properties;

import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.utils.ConfigUnzipper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static io.microconfig.core.configtypes.StandardConfigType.PROCESS;
import static io.microconfig.osdf.microconfig.properties.PropertyGetter.propertyGetter;
import static io.microconfig.osdf.microconfig.properties.PropertySetter.propertySetter;
import static java.nio.file.Path.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PropertyGetterSetterTest {
    private Path DEFAULT_CONFIGS_PATH = of("TODO"); //TODO

    @BeforeEach
    void createConfigs() throws IOException {
        ConfigUnzipper.unzip("configs.zip", DEFAULT_CONFIGS_PATH);
    }

    @Test
    void getSetGet() {
        OSDFPaths paths = null; //TODO
        String oldVersion = propertyGetter(paths).get(PROCESS, "versions", "project.version");
        assertEquals("latest", oldVersion);

        propertySetter().setIfNecessary(of(DEFAULT_CONFIGS_PATH + "/repo/components/system/versions/project-version.proc"), "project.version", "v2");

        String newVersion = propertyGetter(paths).get(PROCESS, "versions", "project.version");
        assertEquals("v2", newVersion);
    }
}